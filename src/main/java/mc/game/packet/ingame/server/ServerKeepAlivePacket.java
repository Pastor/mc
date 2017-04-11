package mc.game.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.Util;

import java.io.IOException;

public class ServerKeepAlivePacket implements Packet {

    private int id;

    @SuppressWarnings("unused")
    private ServerKeepAlivePacket() {
    }

    public ServerKeepAlivePacket(int id) {
        this.id = id;
    }

    public int getPingId() {
        return this.id;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.id = in.readVarInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.id);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return Util.toString(this);
    }
}
