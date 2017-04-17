package mc.minicraft.packet.ingame.client;


import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.ResourcePackStatus;

import java.io.IOException;

public class ClientResourcePackStatusPacket implements Packet {
    private ResourcePackStatus status;

    @SuppressWarnings("unused")
    private ClientResourcePackStatusPacket() {
    }

    public ClientResourcePackStatusPacket(ResourcePackStatus status) {
        this.status = status;
    }

    public ResourcePackStatus getStatus() {
        return this.status;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.status = mc.minicraft.Magic.key(ResourcePackStatus.class, in.readVarInt());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(mc.minicraft.Magic.value(Integer.class, this.status));
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
