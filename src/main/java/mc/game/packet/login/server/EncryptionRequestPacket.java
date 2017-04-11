package mc.game.packet.login.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.crypt.Util;

import java.io.IOException;
import java.security.PublicKey;

public class EncryptionRequestPacket implements Packet {

    private String serverId;
    private PublicKey publicKey;
    private byte verifyToken[];

    @SuppressWarnings("unused")
    private EncryptionRequestPacket() {
    }

    public EncryptionRequestPacket(String serverId, PublicKey publicKey, byte verifyToken[]) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    public String getServerId() {
        return this.serverId;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.serverId = in.readString();
        this.publicKey = Util.decodePublicKey(in.readBytes(in.readVarInt()));
        this.verifyToken = in.readBytes(in.readVarInt());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.serverId);
        byte encoded[] = this.publicKey.getEncoded();
        out.writeVarInt(encoded.length);
        out.writeBytes(encoded);
        out.writeVarInt(this.verifyToken.length);
        out.writeBytes(this.verifyToken);
    }

    @Override
    public boolean isPriority() {
        return true;
    }

    @Override
    public String toString() {
        return mc.game.Util.toString(this);
    }
}
