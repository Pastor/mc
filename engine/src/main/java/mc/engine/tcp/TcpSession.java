package mc.engine.tcp;

import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;
import mc.api.Client;
import mc.api.Packet;
import mc.api.Protocol;
import mc.api.Session;
import mc.engine.EventFactory;

import java.net.ConnectException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

abstract class TcpSession extends SimpleChannelInboundHandler<Packet> implements Session {
    private final EventFactory eventFactory = EventFactory.instance();
    private final String host;
    private final int port;
    private Protocol protocol;

    private int compressionThreshold = -1;
    private int connectTimeout = 30;
    private int readTimeout = 30;
    private int writeTimeout = 0;

    private Map<String, Object> flags = new HashMap<>();
    private List<Session.Listener> listeners = new CopyOnWriteArrayList<>();

    private Channel channel;
    boolean disconnected = false;

    private BlockingQueue<Packet> packets = new LinkedBlockingQueue<>();
    private Thread packetHandleThread;

    TcpSession(String host, int port, Protocol protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    public void connect() {
        this.connect(true);
    }

    public void connect(boolean wait) {
    }

    public void connect(String hostname, boolean wait) {

    }

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    public SocketAddress localAddress() {
        return this.channel != null ? this.channel.localAddress() : null;
    }

    public SocketAddress remoteAddress() {
        return this.channel != null ? this.channel.remoteAddress() : null;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public Map<String, Object> flags() {
        return new HashMap<>(this.flags);
    }

    public boolean hasFlag(String key) {
        return this.flags().containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T flag(String key) {
        Object value = this.flags().get(key);
        if (value == null) {
            return null;
        }

        try {
            return (T) value;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Tried to get flag \"" + key + "\" as the wrong type. Actual type: " + value.getClass().getName());
        }
    }

    public void setFlag(String key, Object value) {
        this.flags.put(key, value);
    }

    public List<Event.Listener> listeners() {
        return new ArrayList<>(this.listeners);
    }

    public void addListener(Session.Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Session.Listener listener) {
        this.listeners.remove(listener);
    }

    public void callEvent(Session.Event event) {
        try {
            for (Session.Listener listener : this.listeners) {
                event.call(listener);
            }
        } catch (Throwable t) {
            exceptionCaught(null, t);
        }
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public void setCompressionThreshold(int threshold) {
        this.compressionThreshold = threshold;
        if (this.channel != null) {
            if (this.compressionThreshold >= 0) {
                if (this.channel.pipeline().get("compression") == null) {
                    this.channel.pipeline().addBefore("codec", "compression", new TcpPacketCompression(this));
                }
            } else if (this.channel.pipeline().get("compression") != null) {
                this.channel.pipeline().remove("compression");
            }
        }
    }

    public int connectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    public int readTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        this.refreshReadTimeoutHandler();
    }

    public int writeTimeout() {
        return this.writeTimeout;
    }

    public void setWriteTimeout(int timeout) {
        this.writeTimeout = timeout;
        this.refreshWriteTimeoutHandler();
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen() && !this.disconnected;
    }

    public void send(final Packet packet) {
        if (this.channel == null) {
            return;
        }
        ChannelFuture future = this.channel.writeAndFlush(packet).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    callEvent(eventFactory.newPacketSentEvent(TcpSession.this, packet));
                } else {
                    exceptionCaught(null, future.cause());
                }
            }
        });
        if (packet.isPriority()) {
            try {
                future.await();
            } catch (InterruptedException e) {
            }
        }
    }

    public void disconnect(String reason) {
        this.disconnect(reason, false);
    }

    public void disconnect(String reason, boolean wait) {
        this.disconnect(reason, null, wait);
    }

    public void disconnect(final String reason, final Throwable cause) {
        this.disconnect(reason, cause, false);
    }

    public void disconnect(final String reason, final Throwable cause, boolean wait) {
        if (this.disconnected) {
            return;
        }
        this.disconnected = true;
        if (this.packetHandleThread != null) {
            this.packetHandleThread.interrupt();
            this.packetHandleThread = null;
        }
        if (this.channel != null && this.channel.isOpen()) {
            this.callEvent(eventFactory.newDisconnectingEvent(this, reason, cause));
            ChannelFuture future = this.channel.flush().close().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    callEvent(eventFactory.newDisconnectedEvent(TcpSession.this, reason != null ? reason : "Connection closed.", cause));
                }
            });
            if (wait) {
                try {
                    future.await();
                } catch (InterruptedException e) {
                }
            }
        } else {
            this.callEvent(eventFactory.newDisconnectedEvent(this, reason != null ? reason : "Connection closed.", cause));
        }
        this.channel = null;
    }

    private void refreshReadTimeoutHandler() {
        this.refreshReadTimeoutHandler(this.channel);
    }

    void refreshReadTimeoutHandler(Channel channel) {
        if (channel != null) {
            if (this.readTimeout <= 0) {
                if (channel.pipeline().get("readTimeout") != null) {
                    channel.pipeline().remove("readTimeout");
                }
            } else {
                if (channel.pipeline().get("readTimeout") == null) {
                    channel.pipeline().addFirst("readTimeout", new ReadTimeoutHandler(this.readTimeout));
                } else {
                    channel.pipeline().replace("readTimeout", "readTimeout", new ReadTimeoutHandler(this.readTimeout));
                }
            }
        }
    }

    private void refreshWriteTimeoutHandler() {
        this.refreshWriteTimeoutHandler(this.channel);
    }

    void refreshWriteTimeoutHandler(Channel channel) {
        if (channel != null) {
            if (this.writeTimeout <= 0) {
                if (channel.pipeline().get("writeTimeout") != null) {
                    channel.pipeline().remove("writeTimeout");
                }
            } else {
                if (channel.pipeline().get("writeTimeout") == null) {
                    channel.pipeline().addFirst("writeTimeout", new WriteTimeoutHandler(this.writeTimeout));
                } else {
                    channel.pipeline().replace("writeTimeout", "writeTimeout", new WriteTimeoutHandler(this.writeTimeout));
                }
            }
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.disconnected || this.channel != null) {
            ctx.channel().close();
            return;
        }
        this.channel = ctx.channel();
        this.packetHandleThread = new Thread(new Runnable() {
            public void run() {
                try {
                    Packet packet;
                    while ((packet = packets.take()) != null) {
                        callEvent(eventFactory.newPacketReceivedEvent(TcpSession.this, packet));
                    }
                } catch (InterruptedException e) {
                } catch (Throwable t) {
                    exceptionCaught(null, t);
                }
            }
        });
        this.packetHandleThread.start();
        this.callEvent(eventFactory.newConnectedEvent(this));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel() == this.channel) {
            this.disconnect("Connection closed.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String message;
        if (cause instanceof ConnectTimeoutException || (cause instanceof ConnectException && cause.getMessage().contains("connection timed out"))) {
            message = "Connection timed out.";
        } else if (cause instanceof ReadTimeoutException) {
            message = "Read timed out.";
        } else if (cause instanceof WriteTimeoutException) {
            message = "Write timed out.";
        } else {
            message = cause.toString();
        }
        this.disconnect(message, cause);
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        if (!packet.isPriority()) {
            this.packets.add(packet);
        }
    }

    static Session.Factory newFactory(Proxy proxy, DefaultFactory factory) {
        return new TcpSessionFactory(proxy, factory);
    }

    private static final class TcpSessionFactory implements Session.Factory {
        private final Proxy proxy;
        private final DefaultFactory factory;

        private TcpSessionFactory(Proxy proxy, DefaultFactory factory) {
            this.proxy = proxy;
            this.factory = factory;
        }

        public Session newSession(Client client) {
            return new TcpClientSession(client.host(), client.port(), client.protocol(),
                    client, proxy, factory);
        }
    }
}
