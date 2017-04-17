package mc.engine.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ConcurrentSet;
import mc.api.*;
import mc.engine.EventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Stream;

public final class DefaultServer implements Server {
    private static final Logger logger = LoggerFactory.getLogger(DefaultServer.class);
    private final EventFactory eventFactory = EventFactory.instance();
    private final String host;
    private final int port;
    private Class<? extends Protocol> protocol;
    private final Session.Factory factory;
    private ConnectionListener listener;
    private final List<Session> sessions = new ArrayList<Session>();

    private final Map<String, Object> flags = new HashMap<String, Object>();
    private final List<Server.Listener> listeners = new ArrayList<Server.Listener>();
    private final PlayerManager manager = new DefaultPlayerManager();

    public DefaultServer(String host, int port, Class<? extends Protocol> protocol, Session.Factory factory) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.factory = factory;
    }

    public Server bind() {
        return this.bind(true);
    }

    @Override
    public void sendBroadcast(Packet packet, Session exclude) {
        sessions.stream().filter(session -> session != exclude).forEach(session -> session.send(packet));
    }

    public Server bind(boolean wait) {
        this.listener = this.newConnectionListener();
        this.listener.bind(wait, () -> callEvent(eventFactory.newServerBoundEvent(DefaultServer.this)));
        return this;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public Class<? extends Protocol> getProtocol() {
        return this.protocol;
    }

    public Protocol createProtocol() {
        try {
            Constructor<? extends Protocol> constructor = this.protocol.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (NoSuchMethodError e) {
            throw new IllegalStateException("PacketProtocol \"" + this.protocol.getName() + "\" does not have a no-params constructor for instantiation.");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate PacketProtocol " + this.protocol.getName() + ".", e);
        }
    }

    public Map<String, Object> getGlobalFlags() {
        return new HashMap<>(this.flags);
    }

    public boolean hasGlobalFlag(String key) {
        return this.flags.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getGlobalFlag(String key) {
        Object value = this.flags.get(key);
        if (value == null) {
            return null;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Tried to get flag \"" + key + "\" as the wrong type. Actual type: " + value.getClass().getName());
        }
    }

    public void setGlobalFlag(String key, Object value) {
        this.flags.put(key, value);
    }

    public List<Server.Listener> getListeners() {
        return new ArrayList<Server.Listener>(this.listeners);
    }

    public void addListener(Server.Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Server.Listener listener) {
        this.listeners.remove(listener);
    }

    private void callEvent(Server.Event event) {
        for (Server.Listener listener : this.listeners) {
            event.call(listener);
        }
    }

    public Stream<Session> sessions() {
        return this.sessions.stream();
    }

    public void addSession(Session session) {
        this.sessions.add(session);
        this.callEvent(eventFactory.newSessionAddedEvent(this, session));
    }

    public void removeSession(Session session) {
        this.sessions.remove(session);
        if (session.isConnected()) {
            session.disconnect("Connection closed.");
        }

        this.callEvent(eventFactory.newSessionRemovedEvent(this, session));
    }

    public boolean isListening() {
        return this.listener != null && this.listener.isListening();
    }

    public void close() {
        this.close(true);
    }

    private void close(boolean wait) {
        this.callEvent(eventFactory.newServerClosingEvent(this));
        this.sessions().filter(Session::isConnected).forEach(session -> {
            session.disconnect("Server closed.");
        });
        this.listener.close(wait, () -> callEvent(eventFactory.newServerClosedEvent(DefaultServer.this)));

    }

    public ConnectionListener newConnectionListener() {
        return new TcpConnectionListener(host, port, this, manager);
    }

    private static final class TcpConnectionListener implements Server.ConnectionListener {
        private final String host;
        private final int port;
        private final Server server;

        private EventLoopGroup group;
        private Channel channel;
        private final PlayerManager manager;

        private TcpConnectionListener(String host, int port, Server server, PlayerManager manager) {
            this.host = host;
            this.port = port;
            this.server = server;
            this.manager = manager;
        }

        public String getHost() {
            return this.host;
        }

        public int getPort() {
            return this.port;
        }

        public boolean isListening() {
            return this.channel != null && this.channel.isOpen();
        }

        public void bind() {
            this.bind(true);
        }

        public void bind(boolean wait) {
            this.bind(wait, null);
        }

        public void bind(boolean wait, final Runnable callback) {
            if (this.group != null || this.channel != null) {
                return;
            }

            this.group = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
            ChannelFuture future = new ServerBootstrap()
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel channel) throws Exception {
                            InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
                            Protocol protocol = server.createProtocol();
                            TcpSession session = new TcpServerSession(
                                    address.getHostName(), address.getPort(), protocol, server);
                            session.protocol().newSession(server, session, manager);

                            channel.config().setOption(ChannelOption.IP_TOS, 0x18);
                            channel.config().setOption(ChannelOption.TCP_NODELAY, false);

                            ChannelPipeline pipeline = channel.pipeline();

                            session.refreshReadTimeoutHandler(channel);
                            session.refreshWriteTimeoutHandler(channel);

                            pipeline.addLast("encryption", new TcpPacketEncryptor(session));
                            pipeline.addLast("sizer", new TcpPacketSizer(session));
                            pipeline.addLast("codec", new TcpPacketCodec(session));
                            pipeline.addLast("manager", session);
                        }
                    }).group(this.group).localAddress(this.host, this.port).bind();

            if (wait) {
                try {
                    future.sync();
                } catch (InterruptedException e) {
                    logger.error("", e);
                }

                channel = future.channel();
                if (callback != null) {
                    callback.run();
                }
            } else {
                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            channel = future.channel();
                            if (callback != null) {
                                callback.run();
                            }
                        } else {
                            logger.error("Failed to asynchronously bind connection listener.");
                            if (future.cause() != null) {
                                logger.error("", future.cause());
                            }
                        }
                    }
                });
            }
        }

        public void close() {
            this.close(false);
        }

        public void close(boolean wait) {
            this.close(wait, null);
        }

        @SuppressWarnings("unchecked")
        public void close(boolean wait, final Runnable callback) {
            if (this.channel != null) {
                if (this.channel.isOpen()) {
                    ChannelFuture future = this.channel.close();
                    if (wait) {
                        try {
                            future.sync();
                        } catch (InterruptedException e) {
                            logger.error("", e);
                        }

                        if (callback != null) {
                            callback.run();
                        }
                    } else {
                        future.addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if (future.isSuccess()) {
                                    if (callback != null) {
                                        callback.run();
                                    }
                                } else {
                                    logger.error("Failed to asynchronously close connection listener.");
                                    if (future.cause() != null) {
                                        logger.error("", future.cause());
                                    }
                                }
                            }
                        });
                    }
                }
                this.channel = null;
            }

            if (this.group != null) {
                Future<?> future = this.group.shutdownGracefully();
                if (wait) {
                    try {
                        future.sync();
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                } else {

                    future.addListener(new GenericFutureListener() {
                        public void operationComplete(Future future) throws Exception {
                            if (!future.isSuccess()) {
                                logger.error("Failed to asynchronously close connection listener.");
                                if (future.cause() != null) {
                                    logger.error("", future.cause());
                                }
                            } else {
                                logger.debug("EventLoopGroup closed");
                            }
                        }
                    });
                }
                this.group = null;
            }
        }
    }

    private static final class DefaultPlayerManager implements PlayerManager {

        private final Set<String> users = new HashSet<>();

        @Override
        public synchronized boolean isLoggined(String username) {
            return users.contains(username);
        }

        @Override
        public synchronized void login(String username) {
            users.add(username);
        }

        @Override
        public synchronized void remove(String username) {
            users.remove(username);
        }
    }
}
