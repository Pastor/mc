package mc.minicraft.packet.ingame.client.world;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientTeleportConfirmPacket implements Packet {
    private int id;

    @SuppressWarnings("unused")
    private ClientTeleportConfirmPacket() {
    }

    public ClientTeleportConfirmPacket(int id) {
        this.id = id;
    }

    public int getTeleportId() {
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
        return mc.minicraft.Util.toString(this);
    }
}
