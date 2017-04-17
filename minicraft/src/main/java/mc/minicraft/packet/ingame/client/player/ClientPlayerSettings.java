package mc.minicraft.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public final class ClientPlayerSettings implements Packet {

    public int visibleDistance;

    public ClientPlayerSettings(int visibleDistance) {
        this.visibleDistance = visibleDistance;
    }

    @SuppressWarnings("unused")
    public ClientPlayerSettings() {
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        visibleDistance = in.readVarInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(visibleDistance);
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
