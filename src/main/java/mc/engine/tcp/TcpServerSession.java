package mc.engine.tcp;

import io.netty.channel.ChannelHandlerContext;
import mc.api.Protocol;
import mc.api.Server;

import java.util.Map;

final class TcpServerSession extends TcpSession {
    private final Server server;

    TcpServerSession(String host, int port, Protocol protocol, Server server) {
        super(host, port, protocol);
        this.server = server;
    }

    @Override
    public Map<String, Object> flags() {
        Map<String, Object> ret = super.flags();
        ret.putAll(this.server.getGlobalFlags());
        return ret;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.server.addSession(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.server.removeSession(this);
    }
}
