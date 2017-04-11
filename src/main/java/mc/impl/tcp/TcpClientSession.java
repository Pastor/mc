package mc.impl.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import mc.api.Client;
import mc.api.Protocol;

import javax.naming.directory.InitialDirContext;
import java.net.Proxy;
import java.util.Hashtable;

final class TcpClientSession extends TcpSession {
    private Client client;
    private Proxy proxy;

    private EventLoopGroup group;

    TcpClientSession(String host, int port, Protocol protocol, Client client, Proxy proxy) {
        super(host, port, protocol);
        this.client = client;
        this.proxy = proxy;
    }

    @Override
    public void connect(boolean wait) {
        if (this.disconnected) {
            throw new IllegalStateException("Session has already been disconnected.");
        } else if (this.group != null) {
            return;
        }

        try {
            final Bootstrap bootstrap = new Bootstrap();
            if (this.proxy != null) {
                this.group = new OioEventLoopGroup();
                bootstrap.channelFactory(DefaultFactory.instance().newChannelFactory(this.proxy));
            } else {
                this.group = new NioEventLoopGroup();
                bootstrap.channel(NioSocketChannel.class);
            }

            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                public void initChannel(Channel channel) throws Exception {
                    protocol().newSession(client, TcpClientSession.this);

                    channel.config().setOption(ChannelOption.IP_TOS, 0x18);
                    channel.config().setOption(ChannelOption.TCP_NODELAY, false);

                    ChannelPipeline pipeline = channel.pipeline();

                    refreshReadTimeoutHandler(channel);
                    refreshWriteTimeoutHandler(channel);

                    pipeline.addLast("encryption", new TcpPacketEncryptor(TcpClientSession.this));
                    pipeline.addLast("sizer", new TcpPacketSizer(TcpClientSession.this));
                    pipeline.addLast("codec", new TcpPacketCodec(TcpClientSession.this));
                    pipeline.addLast("manager", TcpClientSession.this);
                }
            }).group(this.group).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout() * 1000);

            Runnable connectTask = () -> {
                try {
                    String host1 = host();
                    int port1 = port();

                    try {
                        Hashtable<String, String> environment = new Hashtable<>();
                        environment.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
                        environment.put("java.naming.provider.url", "dns:");

                        String[] result = new InitialDirContext(environment)
                                .getAttributes(protocol()
                                        .prefix() + "._tcp." + host1, new String[]{"SRV"})
                                .get("srv").get().toString().split(" ", 4);
                        host1 = result[3];
                        port1 = Integer.parseInt(result[2]);
                    } catch (Throwable t) {
                    }

                    bootstrap.remoteAddress(host1, port1);

                    ChannelFuture future = bootstrap.connect().sync();
                    if (future.isSuccess()) {
                        while (!isConnected() && !disconnected) {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                } catch (Throwable t) {
                    exceptionCaught(null, t);
                }
            };

            if (wait) {
                connectTask.run();
            } else {
                new Thread(connectTask).start();
            }
        } catch (Throwable t) {
            exceptionCaught(null, t);
        }
    }

    @Override
    public void disconnect(String reason, Throwable cause, boolean wait) {
        super.disconnect(reason, cause, wait);
        if (this.group != null) {
            Future<?> future = this.group.shutdownGracefully();
            if (wait) {
                try {
                    future.await();
                } catch (InterruptedException e) {
                }
            }

            this.group = null;
        }
    }
}
