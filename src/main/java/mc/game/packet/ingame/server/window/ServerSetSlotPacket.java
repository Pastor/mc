package mc.game.packet.ingame.server.window;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.metadata.ItemStack;

import java.io.IOException;

public class ServerSetSlotPacket implements Packet {

    private int windowId;
    private int slot;
    private ItemStack item;

    @SuppressWarnings("unused")
    private ServerSetSlotPacket() {
    }

    public ServerSetSlotPacket(int windowId, int slot, ItemStack item) {
        this.windowId = windowId;
        this.slot = slot;
        this.item = item;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.windowId = in.readUnsignedByte();
        this.slot = in.readShort();
        this.item = mc.game.Util.readItem(in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(this.windowId);
        out.writeShort(this.slot);
        mc.game.Util.writeItem(out, this.item);
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
