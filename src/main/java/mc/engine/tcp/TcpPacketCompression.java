package mc.engine.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import mc.api.Buffer;
import mc.api.Session;
import mc.engine.DefaultBuffer;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

final class TcpPacketCompression extends ByteToMessageCodec<ByteBuf> {
    private static final int MAX_COMPRESSED_SIZE = 2097152;

    private final DefaultBuffer buffer = DefaultBuffer.instance();
    private final Session session;
    private final Deflater deflater = new Deflater();
    private final Inflater inflater = new Inflater();
    private byte buf[] = new byte[8192];

    TcpPacketCompression(Session session) {
        this.session = session;
    }

    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int readable = in.readableBytes();
        Buffer.Output output = buffer.newOutput(out);
        if (readable < this.session.getCompressionThreshold()) {
            output.writeVarInt(0);
            out.writeBytes(in);
        } else {
            byte[] bytes = new byte[readable];
            in.readBytes(bytes);
            output.writeVarInt(bytes.length);
            this.deflater.setInput(bytes, 0, readable);
            this.deflater.finish();
            while (!this.deflater.finished()) {
                int length = this.deflater.deflate(this.buf);
                output.writeBytes(this.buf, length);
            }
            this.deflater.reset();
        }
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        if (buf.readableBytes() != 0) {
            Buffer.Input in = buffer.newInput(buf);
            int size = in.readVarInt();
            if (size == 0) {
                out.add(buf.readBytes(buf.readableBytes()));
            } else {
                if (size < this.session.getCompressionThreshold()) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is below threshold of " + this.session.getCompressionThreshold() + ".");
                }
                if (size > MAX_COMPRESSED_SIZE) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is larger than protocol maximum of " + MAX_COMPRESSED_SIZE + ".");
                }
                byte[] bytes = new byte[buf.readableBytes()];
                in.readBytes(bytes);
                this.inflater.setInput(bytes);
                byte[] inflated = new byte[size];
                this.inflater.inflate(inflated);
                out.add(Unpooled.wrappedBuffer(inflated));
                this.inflater.reset();
            }
        }
    }
}
