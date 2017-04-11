package mc.minecraft.packet.ingame.client;


import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientKeepAlivePacket implements Packet {

    private int id;

    @SuppressWarnings("unused")
    private ClientKeepAlivePacket() {
    }

    public ClientKeepAlivePacket(int id) {
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
        return mc.minecraft.Util.toString(this);
    }
}
