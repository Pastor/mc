package mc.minicraft.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.entity.EquipmentSlot;
import mc.minicraft.data.game.entity.metadata.ItemStack;

import java.io.IOException;

public class ServerEntityEquipmentPacket implements Packet {

    private int entityId;
    private EquipmentSlot slot;
    private ItemStack item;

    @SuppressWarnings("unused")
    private ServerEntityEquipmentPacket() {
    }

    public ServerEntityEquipmentPacket(int entityId, EquipmentSlot slot, ItemStack item) {
        this.entityId = entityId;
        this.slot = slot;
        this.item = item;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.slot = mc.minicraft.Magic.key(EquipmentSlot.class, in.readVarInt());
        this.item = mc.minicraft.Util.readItem(in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeVarInt(mc.minicraft.Magic.value(Integer.class, this.slot));
        mc.minicraft.Util.writeItem(out, this.item);
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
