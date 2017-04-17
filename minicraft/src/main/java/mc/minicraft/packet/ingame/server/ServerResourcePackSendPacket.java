package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerResourcePackSendPacket implements Packet {
    private String url;
    private String hash;

    @SuppressWarnings("unused")
    private ServerResourcePackSendPacket() {
    }

    public ServerResourcePackSendPacket(String url, String hash) {
        this.url = url;
        this.hash = hash;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.url = in.readString();
        this.hash = in.readString();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.url);
        out.writeString(this.hash);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.minicraft.Util.toString(this);
    }
}
