package mc.game.packet.ingame.server.entity.player;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerPlayerChangeHeldItemPacket implements Packet {

    private int slot;

    @SuppressWarnings("unused")
    private ServerPlayerChangeHeldItemPacket() {
    }

    public ServerPlayerChangeHeldItemPacket(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.slot = in.readByte();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(this.slot);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.game.Util.toString(this);
    }
}
