package mc.impl;

import mc.api.Packet;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public final class EncryptImpl implements Packet.Encrypt {

    private final Cipher in;
    private final Cipher out;

    public EncryptImpl(Key key)
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
