package mc.engine.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;
import mc.api.Session;
import mc.engine.DefaultBuffer;

import java.util.List;

final class TcpPacketSizer extends ByteToMessageCodec<ByteBuf> {
    private final DefaultBuffer buffer = DefaultBuffer.instance();
    private final Session session;

    TcpPacketSizer(Session session) {
        this.session = session;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int length = in.readableBytes();
        out.ensureWritable(this.session.protocol().get().header().getLengthSize(length) + length);
        this.session.protocol().get().header().writeLength(buffer.newOutput(out), length);
        out.writeBytes(in);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        int size = this.session.protocol().get().header().getLengthSize();
        if (size > 0) {
            buf.markReaderIndex();
            byte[] lengthBytes = new byte[size];
            for (int index = 0; index < lengthBytes.length; index++) {
                if (!buf.isReadable()) {
                    buf.resetReaderIndex();
                    return;
                }

                lengthBytes[index] = buf.readByte();
                if ((this.session.protocol().get().header().isLengthVariable() && lengthBytes[index] >= 0) || index == size - 1) {
                    int length = this.session.protocol().get().header()
                            .readLength(buffer.newInput(Unpooled.wrappedBuffer(lengthBytes)), buf.readableBytes());
                    if (buf.readableBytes() < length) {
                        buf.resetReaderIndex();
                        return;
                    }

                    out.add(buf.readBytes(length));
                    return;
                }
            }

            throw new CorruptedFrameException("Length is too long.");
        } else {
            out.add(buf.readBytes(buf.readableBytes()));
        }
    }
}
