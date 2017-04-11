package mc.engine.tcp;

import io.netty.channel.ChannelFactory;
import io.netty.channel.socket.oio.OioSocketChannel;
import mc.api.Session;

import java.net.Proxy;
import java.net.Socket;

public abstract class DefaultFactory {
    public Session.Factory newSessionFactory(Proxy proxy) {
        return TcpSession.newFactory(proxy, this);
    }

    ChannelFactory<OioSocketChannel> newChannelFactory(Proxy proxy) {
        return new ProxyChannelFactory(proxy);
    }

    public static DefaultFactory instance() {
        return Instance.instance;
    }

    private static final class Instance {
        private static final DefaultFactory instance = new DefaultFactory() {
        };
    }

    private static final class ProxyChannelFactory implements ChannelFactory<OioSocketChannel> {
        private final Proxy proxy;

        private ProxyChannelFactory(Proxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public OioSocketChannel newChannel() {
            return new OioSocketChannel(new Socket(this.proxy));
        }
    }
}
