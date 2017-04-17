package mc.api;

import java.io.IOException;

public interface Packet {

    void read(Buffer.Input in) throws IOException;

    void write(Buffer.Output out) throws IOException;

    boolean isPriority();


    interface Header {
        boolean isLengthVariable();

        int getLengthSize();

        int getLengthSize(int length);

        int readLength(Buffer.Input in, int available) throws IOException;

        void writeLength(Buffer.Output out, int length) throws IOException;

        int readPacketId(Buffer.Input in) throws IOException;

        void writePacketId(Buffer.Output out, int packetId) throws IOException;
    }

    interface Encrypt {
        int getDecryptOutputSize(int length);

        int getEncryptOutputSize(int length);

        int decrypt(byte input[], int inputOffset, int inputLength, byte output[], int outputOffset) throws Exception;

        int encrypt(byte input[], int inputOffset, int inputLength, byte output[], int outputOffset) throws Exception;
    }
}
