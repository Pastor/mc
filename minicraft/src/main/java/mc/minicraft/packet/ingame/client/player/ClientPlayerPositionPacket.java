package mc.minicraft.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public final class ClientPlayerPositionPacket implements Packet {

    public int xa = 0;
    public int ya = 0;

    @Override
    public void read(Buffer.Input in) throws IOException {
        xa = in.readVarInt();
        ya = in.readVarInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(xa);
        out.writeVarInt(ya);
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
