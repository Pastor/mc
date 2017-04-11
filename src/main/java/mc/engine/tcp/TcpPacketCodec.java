package mc.engine.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import mc.api.Buffer;
import mc.api.Packet;
import mc.api.Session;
import mc.engine.DefaultBuffer;
import mc.engine.EventFactory;

import java.util.List;

final class TcpPacketCodec extends ByteToMessageCodec<Packet> {
    private final Session session;
    private final EventFactory eventFactory = EventFactory.instance();
    private final DefaultBuffer buffer = DefaultBuffer.instance();

    TcpPacketCodec(Session session) {
        this.session = session;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buf) throws Exception {
        Buffer.Output out = buffer.newOutput(buf);
        this.session.protocol().header().writePacketId(out, this.session.protocol().getOutgoingId(packet.getClass()));
        packet.write(out);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        int initial = buf.readerIndex();
        Buffer.Input in = buffer.newInput(buf);
        int id = this.session.protocol().header().readPacketId(in);
        if(id == -1) {
            buf.readerIndex(initial);
            return;
        }
        Packet packet = this.session.protocol().createIncomingPacket(id);
        packet.read(in);
        if(buf.readableBytes() > 0) {
            throw new IllegalStateException("Packet \"" + packet.getClass().getSimpleName() + "\" not fully read.");
        }
        if(packet.isPriority()) {
            this.session.callEvent(eventFactory.newPacketReceivedEvent(this.session, packet));
        }
        out.add(packet);
    }
}
