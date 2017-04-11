package mc.minecraft.packet.login.client;


import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.crypt.Util;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class EncryptionResponsePacket implements Packet {

    private byte sharedKey[];
    private byte verifyToken[];

    @SuppressWarnings("unused")
    private EncryptionResponsePacket() {
    }

    public EncryptionResponsePacket(SecretKey secretKey, PublicKey publicKey, byte verifyToken[]) {
        this.sharedKey = Util.encryptData(publicKey, secretKey.getEncoded());
        this.verifyToken = Util.encryptData(publicKey, verifyToken);
    }

    public SecretKey getSecretKey(PrivateKey privateKey) {
        return Util.decryptSharedKey(privateKey, this.sharedKey);
    }

    public byte[] getVerifyToken(PrivateKey privateKey) {
        return Util.decryptData(privateKey, this.verifyToken);
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.sharedKey = in.readBytes(in.readVarInt());
        this.verifyToken = in.readBytes(in.readVarInt());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.sharedKey.length);
        out.writeBytes(this.sharedKey);
        out.writeVarInt(this.verifyToken.length);
        out.writeBytes(this.verifyToken);
    }

    @Override
    public boolean isPriority() {
        return true;
    }

    @Override
    public String toString() {
        return mc.minecraft.Util.toString(this);
    }
}
