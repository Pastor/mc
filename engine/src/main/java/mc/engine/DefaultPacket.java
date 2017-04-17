package mc.engine;

import mc.api.Buffer;
import mc.api.Packet;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public abstract class DefaultPacket implements Packet {

    public static final class Encrypt implements Packet.Encrypt {

        private final Cipher in;
        private final Cipher out;

        public Encrypt(Key key)
                throws NoSuchPaddingException, NoSuchAlgorithmException,
                InvalidAlgorithmParameterException, InvalidKeyException {
            this.in = Cipher.getInstance("AES/CFB8/NoPadding");
            this.in.init(2, key, new IvParameterSpec(key.getEncoded()));
            this.out = Cipher.getInstance("AES/CFB8/NoPadding");
            this.out.init(1, key, new IvParameterSpec(key.getEncoded()));
        }

        public int getDecryptOutputSize(int length) {
            return this.in.getOutputSize(length);
        }

        public int getEncryptOutputSize(int length) {
            return this.out.getOutputSize(length);
        }

        public int decrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset)
                throws Exception {
            return this.in.update(input, inputOffset, inputLength, output, outputOffset);
        }

        public int encrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset)
                throws Exception {
            return this.out.update(input, inputOffset, inputLength, output, outputOffset);
        }
    }

    public static final class Header implements Packet.Header {

        public boolean isLengthVariable() {
            return true;
        }

        public int getLengthSize() {
            return 5;
        }

        public int getLengthSize(int length) {
            if ((length & -128) == 0) {
                return 1;
            } else if ((length & -16384) == 0) {
                return 2;
            } else if ((length & -2097152) == 0) {
                return 3;
            } else if ((length & -268435456) == 0) {
                return 4;
            } else {
                return 5;
            }
        }

        public int readLength(Buffer.Input in, int available) throws IOException {
            return in.readVarInt();
        }

        public void writeLength(Buffer.Output out, int length) throws IOException {
            out.writeVarInt(length);
        }

        public int readPacketId(Buffer.Input in) throws IOException {
            return in.readVarInt();
        }

        public void writePacketId(Buffer.Output out, int packetId) throws IOException {
            out.writeVarInt(packetId);
        }
    }

}
