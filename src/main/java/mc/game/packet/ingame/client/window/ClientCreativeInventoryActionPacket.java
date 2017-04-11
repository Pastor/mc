package mc.game.packet.ingame.client.window;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.metadata.ItemStack;

import java.io.IOException;

public class ClientCreativeInventoryActionPacket implements Packet {

    private int slot;
    private ItemStack clicked;

    @SuppressWarnings("unused")
    private ClientCreativeInventoryActionPacket() {
    }

    public ClientCreativeInventoryActionPacket(int slot, ItemStack clicked) {
        this.slot = slot;
        this.clicked = clicked;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getClickedItem() {
        return this.clicked;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.slot = in.readShort();
        this.clicked = mc.game.Util.readItem(in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeShort(this.slot);
        mc.game.Util.writeItem(out, this.clicked);
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
