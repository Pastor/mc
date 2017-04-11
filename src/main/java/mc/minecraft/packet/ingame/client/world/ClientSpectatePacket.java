package mc.minecraft.packet.ingame.client.world;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;
import java.util.UUID;

public class ClientSpectatePacket implements Packet {
    private UUID target;

    @SuppressWarnings("unused")
    private ClientSpectatePacket() {
    }

    public ClientSpectatePacket(UUID target) {
        this.target = target;
    }

    public UUID getTarget() {
        return this.target;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.target = in.readUUID();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeUUID(this.target);
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
