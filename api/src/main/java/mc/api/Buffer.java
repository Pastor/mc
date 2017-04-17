package mc.api;

import java.io.IOException;
import java.util.UUID;

public interface Buffer {

    interface Input {
        boolean readBoolean() throws IOException;

        byte readByte() throws IOException;

        int readUnsignedByte() throws IOException;

        short readShort() throws IOException;

        int readUnsignedShort() throws IOException;

        char readChar() throws IOException;

        int readInt() throws IOException;

        int readVarInt() throws IOException;

        long readLong() throws IOException;

        long readVarLong() throws IOException;

        float readFloat() throws IOException;

        double readDouble() throws IOException;

        byte[] readBytes(int length) throws IOException;

        int readBytes(byte b[]) throws IOException;

        int readBytes(byte b[], int offset, int length) throws IOException;

        short[] readShorts(int length) throws IOException;

        int readShorts(short s[]) throws IOException;

        int readShorts(short s[], int offset, int length) throws IOException;

        int[] readInts(int length) throws IOException;

        int readInts(int i[]) throws IOException;

        int readInts(int i[], int offset, int length) throws IOException;

        long[] readLongs(int length) throws IOException;

        int readLongs(long l[]) throws IOException;

        int readLongs(long l[], int offset, int length) throws IOException;

        String readString() throws IOException;

        UUID readUUID() throws IOException;

        int available() throws IOException;
    }

    interface Output {

        void writeBoolean(boolean b) throws IOException;

        void writeByte(int b) throws IOException;

        void writeShort(int s) throws IOException;

        void writeChar(int c) throws IOException;

        void writeInt(int i) throws IOException;

        void writeVarInt(int i) throws IOException;

        void writeLong(long l) throws IOException;

        void writeVarLong(long l) throws IOException;

        void writeFloat(float f) throws IOException;

        void writeDouble(double d) throws IOException;

        void writeBytes(byte b[]) throws IOException;

        void writeBytes(byte b[], int length) throws IOException;

        void writeShorts(short s[]) throws IOException;

        void writeShorts(short s[], int length) throws IOException;

        void writeInts(int i[]) throws IOException;

        void writeInts(int i[], int length) throws IOException;

        void writeLongs(long l[]) throws IOException;

        void writeLongs(long l[], int length) throws IOException;

        void writeString(String s) throws IOException;

        void writeUUID(UUID uuid) throws IOException;

        void flush() throws IOException;
    }
}
