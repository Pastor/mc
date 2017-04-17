package mc.minicraft.packet.ingame.server.window;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.entity.metadata.ItemStack;

import java.io.IOException;

public class ServerWindowItemsPacket implements Packet {

    private int windowId;
    private ItemStack items[];

    @SuppressWarnings("unused")
    private ServerWindowItemsPacket() {
    }

    public ServerWindowItemsPacket(int windowId, ItemStack items[]) {
        this.windowId = windowId;
        this.items = items;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public ItemStack[] getItems() {
        return this.items;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.windowId = in.readUnsignedByte();
        this.items = new ItemStack[in.readShort()];
        for (int index = 0; index < this.items.length; index++) {
            this.items[index] = mc.minicraft.Util.readItem(in);
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(this.windowId);
        out.writeShort(this.items.length);
        for (ItemStack item : this.items) {
            mc.minicraft.Util.writeItem(out, item);
        }
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
