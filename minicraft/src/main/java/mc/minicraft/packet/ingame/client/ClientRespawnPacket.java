package mc.minicraft.packet.ingame.client;


import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.engine.entity.Player;

import java.io.IOException;
import java.util.UUID;

public final class ClientRespawnPacket implements Packet {

    public UUID id;

    @SuppressWarnings("unused")
    private ClientRespawnPacket() {

    }

    public ClientRespawnPacket(UUID id) {
        this.id = id;
    }


    @Override
    public void read(Buffer.Input in) throws IOException {
        id = UUID.fromString(in.readString());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(id.toString());
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
