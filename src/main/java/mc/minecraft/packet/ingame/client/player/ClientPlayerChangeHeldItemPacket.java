package mc.minecraft.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientPlayerChangeHeldItemPacket implements Packet {

    private int slot;

    @SuppressWarnings("unused")
    private ClientPlayerChangeHeldItemPacket() {
    }

    public ClientPlayerChangeHeldItemPacket(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.slot = in.readShort();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeShort(this.slot);
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
