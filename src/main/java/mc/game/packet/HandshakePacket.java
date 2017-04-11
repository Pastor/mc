package mc.game.packet;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.Magic;
import mc.game.MinecraftProtocol;
import mc.game.Util;

import java.io.IOException;

public final class HandshakePacket implements Packet {
    private int protocolVersion;
    private String hostname;
    private int port;
    private MinecraftProtocol.HandshakeIntent intent;

    @SuppressWarnings("unused")
    private HandshakePacket() {
    }

    public HandshakePacket(int protocolVersion, String hostname, int port, MinecraftProtocol.HandshakeIntent intent) {
        this.protocolVersion = protocolVersion;
        this.hostname = hostname;
        this.port = port;
        this.intent = intent;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getHostName() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public MinecraftProtocol.HandshakeIntent getIntent() {
        return this.intent;
    }

    public void read(Buffer.Input in) throws IOException {
        this.protocolVersion = in.readVarInt();
        this.hostname = in.readString();
        this.port = in.readUnsignedShort();
        this.intent = Magic.key(MinecraftProtocol.HandshakeIntent.class, in.readVarInt());
    }

    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.protocolVersion);
        out.writeString(this.hostname);
        out.writeShort(this.port);
        out.writeVarInt(Magic.value(Integer.class, this.intent));
    }

    public boolean isPriority() {
        return true;
    }

    @Override
    public String toString() {
        return Util.toString(this);
    }
}
