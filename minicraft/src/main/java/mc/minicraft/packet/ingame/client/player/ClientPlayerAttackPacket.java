package mc.minicraft.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public final class ClientPlayerAttackPacket implements Packet {

    public boolean attack = true;

    @Override
    public void read(Buffer.Input in) throws IOException {
        attack = in.readBoolean();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeBoolean(attack);
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
