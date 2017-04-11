package mc.impl;

import io.netty.buffer.ByteBuf;
import mc.api.Buffer;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

public abstract class DefaultBuffer implements Buffer {

    private static final class Instance {
        private static final DefaultBuffer instance = new DefaultBuffer() {
        };
    }

    public static DefaultBuffer instance() {
        return Instance.instance;
    }

    public final Buffer.Input newInput(ByteBuffer buffer) {
        return new DefaultByteBufferInput(buffer);
    }

    public final Buffer.Output newOutput(ByteBuffer buffer) {
        return new DefaultByteBufferOutput(buffer);
    }

    public final Buffer.Input newInput(InputStream stream) {
        return new DefaultInputStream(stream);
    }

    public final Buffer.Output newOutput(OutputStream stream) {
        return new DefaultOutputStream(stream);
    }

    public final Buffer.Input newInput(ByteBuf buffer) {
        return new DefaultByteBufInput(buffer);
    }

    public final Buffer.Output newOutput(ByteBuf buffer) {
        return new DefaultByteBufOutput(buffer);
    }

    private static final class DefaultByteBufferInput implements Buffer.Input {
        private final ByteBuffer buffer;

        private DefaultByteBufferInput(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        public ByteBuffer getByteBuffer() {
            return this.buffer;
        }

        public boolean readBoolean() throws IOException {
            return this.buffer.get() == 1;
        }

        public byte readByte() throws IOException {
            return this.buffer.get();
        }

        public int readUnsignedByte() throws IOException {
            return this.buffer.get() & 0xFF;
        }

        public short readShort() throws IOException {
            return this.buffer.getShort();
        }

        public int readUnsignedShort() throws IOException {
            return this.buffer.getShort() & 0xFFFF;
        }

        public char readChar() throws IOException {
            return this.buffer.getChar();
        }

        public int readInt() throws IOException {
            return this.buffer.getInt();
        }

        public int readVarInt() throws IOException {
            int value = 0;
            int size = 0;
            int b;
            while (((b = this.readByte()) & 0x80) == 0x80) {
                value |= (b & 0x7F) << (size++ * 7);
                if (size > 5) {
                    throw new IOException("VarInt too long (length must be <= 5)");
                }
            }

            return value | ((b & 0x7F) << (size * 7));
        }

        public long readLong() throws IOException {
            return this.buffer.getLong();
        }

        public long readVarLong() throws IOException {
            long value = 0;
            int size = 0;
            int b;
            while (((b = this.readByte()) & 0x80) == 0x80) {
                value |= (long) (b & 0x7F) << (size++ * 7);
                if (size > 10) {
                    throw new IOException("VarLong too long (length must be <= 10)");
                }
            }

            return value | ((long) (b & 0x7F) << (size * 7));
        }

        public float readFloat() throws IOException {
            return this.buffer.getFloat();
        }

        public double readDouble() throws IOException {
            return this.buffer.getDouble();
        }

        public byte[] readBytes(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            byte b[] = new byte[length];
            this.buffer.get(b);
            return b;
        }

        public int readBytes(byte[] b) throws IOException {
            return this.readBytes(b, 0, b.length);
        }

        public int readBytes(byte[] b, int offset, int length) throws IOException {
            int readable = this.buffer.remaining();
            if (readable <= 0) {
                return -1;
            }

            if (readable < length) {
                length = readable;
            }

            this.buffer.get(b, offset, length);
            return length;
        }

        public short[] readShorts(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            short s[] = new short[length];
            for (int index = 0; index < length; index++) {
                s[index] = this.readShort();
            }

            return s;
        }

        public int readShorts(short[] s) throws IOException {
            return this.readShorts(s, 0, s.length);
        }

        public int readShorts(short[] s, int offset, int length) throws IOException {
            int readable = this.buffer.remaining();
            if (readable <= 0) {
                return -1;
            }

            if (readable < length * 2) {
                length = readable / 2;
            }

            for (int index = offset; index < offset + length; index++) {
                s[index] = this.readShort();
            }

            return length;
        }

        public int[] readInts(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            int i[] = new int[length];
            for (int index = 0; index < length; index++) {
                i[index] = this.readInt();
            }

            return i;
        }

        public int readInts(int[] i) throws IOException {
            return this.readInts(i, 0, i.length);
        }

        public int readInts(int[] i, int offset, int length) throws IOException {
            int readable = this.buffer.remaining();
            if (readable <= 0) {
                return -1;
            }

            if (readable < length * 4) {
                length = readable / 4;
            }

            for (int index = offset; index < offset + length; index++) {
                i[index] = this.readInt();
            }

            return length;
        }

        public long[] readLongs(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            long l[] = new long[length];
            for (int index = 0; index < length; index++) {
                l[index] = this.readLong();
            }

            return l;
        }

        public int readLongs(long[] l) throws IOException {
            return this.readLongs(l, 0, l.length);
        }

        public int readLongs(long[] l, int offset, int length) throws IOException {
            int readable = this.buffer.remaining();
            if (readable <= 0) {
                return -1;
            }

            if (readable < length * 2) {
                length = readable / 2;
            }

            for (int index = offset; index < offset + length; index++) {
                l[index] = this.readLong();
            }

            return length;
        }

        public String readString() throws IOException {
            int length = this.readVarInt();
            byte bytes[] = this.readBytes(length);
            return new String(bytes, "UTF-8");
        }

        public UUID readUUID() throws IOException {
            return new UUID(this.readLong(), this.readLong());
        }

        public int available() throws IOException {
            return this.buffer.remaining();
        }
    }

    private static final class DefaultByteBufferOutput implements Buffer.Output {
        private final ByteBuffer buffer;

        private DefaultByteBufferOutput(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        public ByteBuffer getByteBuffer() {
            return this.buffer;
        }

        public void writeBoolean(boolean b) throws IOException {
            this.buffer.put(b ? (byte) 1 : 0);
        }

        public void writeByte(int b) throws IOException {
            this.buffer.put((byte) b);
        }

        public void writeShort(int s) throws IOException {
            this.buffer.putShort((short) s);
        }

        public void writeChar(int c) throws IOException {
            this.buffer.putChar((char) c);
        }

        public void writeInt(int i) throws IOException {
            this.buffer.putInt(i);
        }

        public void writeVarInt(int i) throws IOException {
            while ((i & ~0x7F) != 0) {
                this.writeByte((i & 0x7F) | 0x80);
                i >>>= 7;
            }

            this.writeByte(i);
        }

        public void writeLong(long l) throws IOException {
            this.buffer.putLong(l);
        }

        public void writeVarLong(long l) throws IOException {
            while ((l & ~0x7F) != 0) {
                this.writeByte((int) (l & 0x7F) | 0x80);
                l >>>= 7;
            }

            this.writeByte((int) l);
        }

        public void writeFloat(float f) throws IOException {
            this.buffer.putFloat(f);
        }

        public void writeDouble(double d) throws IOException {
            this.buffer.putDouble(d);
        }

        public void writeBytes(byte b[]) throws IOException {
            this.buffer.put(b);
        }

        public void writeBytes(byte b[], int length) throws IOException {
            this.buffer.put(b, 0, length);
        }

        public void writeShorts(short[] s) throws IOException {
            this.writeShorts(s, s.length);
        }

        public void writeShorts(short[] s, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeShort(s[index]);
            }
        }

        public void writeInts(int[] i) throws IOException {
            this.writeInts(i, i.length);
        }

        public void writeInts(int[] i, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeInt(i[index]);
            }
        }

        public void writeLongs(long[] l) throws IOException {
            this.writeLongs(l, l.length);
        }

        public void writeLongs(long[] l, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeLong(l[index]);
            }
        }

        public void writeString(String s) throws IOException {
            if (s == null) {
                throw new IllegalArgumentException("String cannot be null!");
            }

            byte[] bytes = s.getBytes("UTF-8");
            if (bytes.length > 32767) {
                throw new IOException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
            } else {
                this.writeVarInt(bytes.length);
                this.writeBytes(bytes);
            }
        }

        public void writeUUID(UUID uuid) throws IOException {
            this.writeLong(uuid.getMostSignificantBits());
            this.writeLong(uuid.getLeastSignificantBits());
        }

        public void flush() throws IOException {
        }
    }

    private static final class DefaultInputStream implements Buffer.Input {
        private final InputStream in;

        private DefaultInputStream(InputStream in) {
            this.in = in;
        }

        public boolean readBoolean() throws IOException {
            return this.readByte() == 1;
        }

        public byte readByte() throws IOException {
            return (byte) this.readUnsignedByte();
        }

        public int readUnsignedByte() throws IOException {
            int b = this.in.read();
            if (b < 0) {
                throw new EOFException();
            }

            return b;
        }

        public short readShort() throws IOException {
            return (short) this.readUnsignedShort();
        }

        public int readUnsignedShort() throws IOException {
            int ch1 = this.readUnsignedByte();
            int ch2 = this.readUnsignedByte();
            return (ch1 << 8) + (ch2 << 0);
        }

        public char readChar() throws IOException {
            int ch1 = this.readUnsignedByte();
            int ch2 = this.readUnsignedByte();
            return (char) ((ch1 << 8) + (ch2 << 0));
        }

        public int readInt() throws IOException {
            int ch1 = this.readUnsignedByte();
            int ch2 = this.readUnsignedByte();
            int ch3 = this.readUnsignedByte();
            int ch4 = this.readUnsignedByte();
            return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
        }

        public int readVarInt() throws IOException {
            int value = 0;
            int size = 0;
            int b;
            while (((b = this.readByte()) & 0x80) == 0x80) {
                value |= (b & 0x7F) << (size++ * 7);
                if (size > 5) {
                    throw new IOException("VarInt too long (length must be <= 5)");
                }
            }

            return value | ((b & 0x7F) << (size * 7));
        }

        public long readLong() throws IOException {
            byte read[] = this.readBytes(8);
            return ((long) read[0] << 56) + ((long) (read[1] & 255) << 48) + ((long) (read[2] & 255) << 40) + ((long) (read[3] & 255) << 32) + ((long) (read[4] & 255) << 24) + ((read[5] & 255) << 16) + ((read[6] & 255) << 8) + ((read[7] & 255) << 0);
        }

        public long readVarLong() throws IOException {
            long value = 0;
            int size = 0;
            int b;
            while (((b = this.readByte()) & 0x80) == 0x80) {
                value |= (long) (b & 0x7F) << (size++ * 7);
                if (size > 10) {
                    throw new IOException("VarLong too long (length must be <= 10)");
                }
            }

            return value | ((long) (b & 0x7F) << (size * 7));
        }

        public float readFloat() throws IOException {
            return Float.intBitsToFloat(this.readInt());
        }

        public double readDouble() throws IOException {
            return Double.longBitsToDouble(this.readLong());
        }

        public byte[] readBytes(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            byte b[] = new byte[length];
            int n = 0;
            while (n < length) {
                int count = this.in.read(b, n, length - n);
                if (count < 0) {
                    throw new EOFException();
                }

                n += count;
            }

            return b;
        }

        public int readBytes(byte[] b) throws IOException {
            return this.in.read(b);
        }

        public int readBytes(byte[] b, int offset, int length) throws IOException {
            return this.in.read(b, offset, length);
        }

        public short[] readShorts(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            short s[] = new short[length];
            int read = this.readShorts(s);
            if (read < length) {
                throw new EOFException();
            }

            return s;
        }

        public int readShorts(short[] s) throws IOException {
            return this.readShorts(s, 0, s.length);
        }

        public int readShorts(short[] s, int offset, int length) throws IOException {
            for (int index = offset; index < offset + length; index++) {
                try {
                    s[index] = this.readShort();
                } catch (EOFException e) {
                    return index - offset;
                }
            }

            return length;
        }

        public int[] readInts(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            int i[] = new int[length];
            int read = this.readInts(i);
            if (read < length) {
                throw new EOFException();
            }

            return i;
        }

        public int readInts(int[] i) throws IOException {
            return this.readInts(i, 0, i.length);
        }

        public int readInts(int[] i, int offset, int length) throws IOException {
            for (int index = offset; index < offset + length; index++) {
                try {
                    i[index] = this.readInt();
                } catch (EOFException e) {
                    return index - offset;
                }
            }

            return length;
        }

        public long[] readLongs(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            long l[] = new long[length];
            int read = this.readLongs(l);
            if (read < length) {
                throw new EOFException();
            }

            return l;
        }

        public int readLongs(long[] l) throws IOException {
            return this.readLongs(l, 0, l.length);
        }

        public int readLongs(long[] l, int offset, int length) throws IOException {
            for (int index = offset; index < offset + length; index++) {
                try {
                    l[index] = this.readLong();
                } catch (EOFException e) {
                    return index - offset;
                }
            }

            return length;
        }

        public String readString() throws IOException {
            int length = this.readVarInt();
            byte bytes[] = this.readBytes(length);
            return new String(bytes, "UTF-8");
        }

        public UUID readUUID() throws IOException {
            return new UUID(this.readLong(), this.readLong());
        }

        public int available() throws IOException {
            return this.in.available();
        }
    }

    private static final class DefaultOutputStream implements Buffer.Output {
        private final OutputStream out;

        private DefaultOutputStream(OutputStream out) {
            this.out = out;
        }

        public void writeBoolean(boolean b) throws IOException {
            this.writeByte(b ? 1 : 0);
        }

        public void writeByte(int b) throws IOException {
            this.out.write(b);
        }

        public void writeShort(int s) throws IOException {
            this.writeByte((byte) ((s >>> 8) & 0xFF));
            this.writeByte((byte) ((s >>> 0) & 0xFF));
        }

        public void writeChar(int c) throws IOException {
            this.writeByte((byte) ((c >>> 8) & 0xFF));
            this.writeByte((byte) ((c >>> 0) & 0xFF));
        }

        public void writeInt(int i) throws IOException {
            this.writeByte((byte) ((i >>> 24) & 0xFF));
            this.writeByte((byte) ((i >>> 16) & 0xFF));
            this.writeByte((byte) ((i >>> 8) & 0xFF));
            this.writeByte((byte) ((i >>> 0) & 0xFF));
        }

        public void writeVarInt(int i) throws IOException {
            while ((i & ~0x7F) != 0) {
                this.writeByte((i & 0x7F) | 0x80);
                i >>>= 7;
            }

            this.writeByte(i);
        }

        public void writeLong(long l) throws IOException {
            this.writeByte((byte) (l >>> 56));
            this.writeByte((byte) (l >>> 48));
            this.writeByte((byte) (l >>> 40));
            this.writeByte((byte) (l >>> 32));
            this.writeByte((byte) (l >>> 24));
            this.writeByte((byte) (l >>> 16));
            this.writeByte((byte) (l >>> 8));
            this.writeByte((byte) (l >>> 0));
        }

        public void writeVarLong(long l) throws IOException {
            while ((l & ~0x7F) != 0) {
                this.writeByte((int) (l & 0x7F) | 0x80);
                l >>>= 7;
            }

            this.writeByte((int) l);
        }

        public void writeFloat(float f) throws IOException {
            this.writeInt(Float.floatToIntBits(f));
        }

        public void writeDouble(double d) throws IOException {
            this.writeLong(Double.doubleToLongBits(d));
        }

        public void writeBytes(byte b[]) throws IOException {
            this.writeBytes(b, b.length);
        }

        public void writeBytes(byte b[], int length) throws IOException {
            this.out.write(b, 0, length);
        }

        public void writeShorts(short[] s) throws IOException {
            this.writeShorts(s, s.length);
        }

        public void writeShorts(short[] s, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeShort(s[index]);
            }
        }

        public void writeInts(int[] i) throws IOException {
            this.writeInts(i, i.length);
        }

        public void writeInts(int[] i, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeInt(i[index]);
            }
        }

        public void writeLongs(long[] l) throws IOException {
            this.writeLongs(l, l.length);
        }

        public void writeLongs(long[] l, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeLong(l[index]);
            }
        }

        public void writeString(String s) throws IOException {
            if (s == null) {
                throw new IllegalArgumentException("String cannot be null!");
            }

            byte[] bytes = s.getBytes("UTF-8");
            if (bytes.length > 32767) {
                throw new IOException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
            } else {
                this.writeVarInt(bytes.length);
                this.writeBytes(bytes);
            }
        }

        public void writeUUID(UUID uuid) throws IOException {
            this.writeLong(uuid.getMostSignificantBits());
            this.writeLong(uuid.getLeastSignificantBits());
        }

        public void flush() throws IOException {
            this.out.flush();
        }
    }

    private static final class DefaultByteBufInput implements Buffer.Input {
        private final ByteBuf buf;

        private DefaultByteBufInput(ByteBuf buf) {
            this.buf = buf;
        }

        public boolean readBoolean() throws IOException {
            return this.buf.readBoolean();
        }

        public byte readByte() throws IOException {
            return this.buf.readByte();
        }

        public int readUnsignedByte() throws IOException {
            return this.buf.readUnsignedByte();
        }

        public short readShort() throws IOException {
            return this.buf.readShort();
        }

        public int readUnsignedShort() throws IOException {
            return this.buf.readUnsignedShort();
        }

        public char readChar() throws IOException {
            return this.buf.readChar();
        }

        public int readInt() throws IOException {
            return this.buf.readInt();
        }

        public int readVarInt() throws IOException {
            int value = 0;
            int size = 0;
            int b;
            while (((b = this.readByte()) & 0x80) == 0x80) {
                value |= (b & 0x7F) << (size++ * 7);
                if (size > 5) {
                    throw new IOException("VarInt too long (length must be <= 5)");
                }
            }

            return value | ((b & 0x7F) << (size * 7));
        }

        public long readLong() throws IOException {
            return this.buf.readLong();
        }

        public long readVarLong() throws IOException {
            int value = 0;
            int size = 0;
            int b;
            while (((b = this.readByte()) & 0x80) == 0x80) {
                value |= (b & 0x7F) << (size++ * 7);
                if (size > 10) {
                    throw new IOException("VarLong too long (length must be <= 10)");
                }
            }
            return value | ((b & 0x7F) << (size * 7));
        }

        public float readFloat() throws IOException {
            return this.buf.readFloat();
        }

        public double readDouble() throws IOException {
            return this.buf.readDouble();
        }

        public byte[] readBytes(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            byte b[] = new byte[length];
            this.buf.readBytes(b);
            return b;
        }

        public int readBytes(byte[] b) throws IOException {
            return this.readBytes(b, 0, b.length);
        }

        public int readBytes(byte[] b, int offset, int length) throws IOException {
            int readable = this.buf.readableBytes();
            if (readable <= 0) {
                return -1;
            }

            if (readable < length) {
                length = readable;
            }

            this.buf.readBytes(b, offset, length);
            return length;
        }

        public short[] readShorts(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            short s[] = new short[length];
            for (int index = 0; index < length; index++) {
                s[index] = this.readShort();
            }

            return s;
        }

        public int readShorts(short[] s) throws IOException {
            return this.readShorts(s, 0, s.length);
        }

        public int readShorts(short[] s, int offset, int length) throws IOException {
            int readable = this.buf.readableBytes();
            if (readable <= 0) {
                return -1;
            }

            if (readable < length * 2) {
                length = readable / 2;
            }

            for (int index = offset; index < offset + length; index++) {
                s[index] = this.readShort();
            }

            return length;
        }

        public int[] readInts(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            int i[] = new int[length];
            for (int index = 0; index < length; index++) {
                i[index] = this.readInt();
            }

            return i;
        }

        public int readInts(int[] i) throws IOException {
            return this.readInts(i, 0, i.length);
        }

        public int readInts(int[] i, int offset, int length) throws IOException {
            int readable = this.buf.readableBytes();
            if (readable <= 0) {
                return -1;
            }

            if (readable < length * 4) {
                length = readable / 4;
            }

            for (int index = offset; index < offset + length; index++) {
                i[index] = this.readInt();
            }

            return length;
        }

        public long[] readLongs(int length) throws IOException {
            if (length < 0) {
                throw new IllegalArgumentException("Array cannot have length less than 0.");
            }

            long l[] = new long[length];
            for (int index = 0; index < length; index++) {
                l[index] = this.readLong();
            }

            return l;
        }

        public int readLongs(long[] l) throws IOException {
            return this.readLongs(l, 0, l.length);
        }

        public int readLongs(long[] l, int offset, int length) throws IOException {
            int readable = this.buf.readableBytes();
            if (readable <= 0) {
                return -1;
            }

            if (readable < length * 2) {
                length = readable / 2;
            }

            for (int index = offset; index < offset + length; index++) {
                l[index] = this.readLong();
            }

            return length;
        }

        public String readString() throws IOException {
            int length = this.readVarInt();
            byte bytes[] = this.readBytes(length);
            return new String(bytes, "UTF-8");
        }

        public UUID readUUID() throws IOException {
            return new UUID(this.readLong(), this.readLong());
        }

        public int available() throws IOException {
            return this.buf.readableBytes();
        }
    }

    private static final class DefaultByteBufOutput implements Buffer.Output {
        private final ByteBuf buf;

        private DefaultByteBufOutput(ByteBuf buf) {
            this.buf = buf;
        }

        public void writeBoolean(boolean b) throws IOException {
            this.buf.writeBoolean(b);
        }

        public void writeByte(int b) throws IOException {
            this.buf.writeByte(b);
        }

        public void writeShort(int s) throws IOException {
            this.buf.writeShort(s);
        }

        public void writeChar(int c) throws IOException {
            this.buf.writeChar(c);
        }

        public void writeInt(int i) throws IOException {
            this.buf.writeInt(i);
        }

        public void writeVarInt(int i) throws IOException {
            while ((i & ~0x7F) != 0) {
                this.writeByte((i & 0x7F) | 0x80);
                i >>>= 7;
            }
            this.writeByte(i);
        }

        public void writeLong(long l) throws IOException {
            this.buf.writeLong(l);
        }

        public void writeVarLong(long l) throws IOException {
            while ((l & ~0x7F) != 0) {
                this.writeByte((int) (l & 0x7F) | 0x80);
                l >>>= 7;
            }

            this.writeByte((int) l);
        }

        public void writeFloat(float f) throws IOException {
            this.buf.writeFloat(f);
        }

        public void writeDouble(double d) throws IOException {
            this.buf.writeDouble(d);
        }

        public void writeBytes(byte b[]) throws IOException {
            this.buf.writeBytes(b);
        }

        public void writeBytes(byte b[], int length) throws IOException {
            this.buf.writeBytes(b, 0, length);
        }

        public void writeShorts(short[] s) throws IOException {
            this.writeShorts(s, s.length);
        }

        public void writeShorts(short[] s, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeShort(s[index]);
            }
        }

        public void writeInts(int[] i) throws IOException {
            this.writeInts(i, i.length);
        }

        public void writeInts(int[] i, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeInt(i[index]);
            }
        }

        public void writeLongs(long[] l) throws IOException {
            this.writeLongs(l, l.length);
        }

        public void writeLongs(long[] l, int length) throws IOException {
            for (int index = 0; index < length; index++) {
                this.writeLong(l[index]);
            }
        }

        public void writeString(String s) throws IOException {
            if (s == null) {
                throw new IllegalArgumentException("String cannot be null!");
            }

            byte[] bytes = s.getBytes("UTF-8");
            if (bytes.length > 32767) {
                throw new IOException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
            } else {
                this.writeVarInt(bytes.length);
                this.writeBytes(bytes);
            }
        }

        public void writeUUID(UUID uuid) throws IOException {
            this.writeLong(uuid.getMostSignificantBits());
            this.writeLong(uuid.getLeastSignificantBits());
        }

        public void flush() throws IOException {
        }
    }
}
