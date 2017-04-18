package mc.minicraft.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public final class ClientPlayerUpdatePacket implements Packet {

    public int xa;
    public int ya;
    public int dir;

    @Override
    public void read(Buffer.Input in) throws IOException {
        xa = in.readVarInt();
        ya = in.readVarInt();
        dir = in.readVarInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(xa);
        out.writeVarInt(ya);
        out.writeVarInt(dir);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return "ClientPlayerUpdatePacket{" +
                "xa=" + xa +
                ", ya=" + ya +
                ", dir=" + dir +
                '}';
    }
}
