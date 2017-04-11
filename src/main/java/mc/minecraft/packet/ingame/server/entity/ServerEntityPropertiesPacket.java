package mc.minecraft.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.attribute.Attribute;
import mc.minecraft.data.game.entity.attribute.AttributeModifier;
import mc.minecraft.data.game.entity.attribute.AttributeType;
import mc.minecraft.data.game.entity.attribute.ModifierOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerEntityPropertiesPacket implements Packet {

    private int entityId;
    private List<Attribute> attributes;

    @SuppressWarnings("unused")
    private ServerEntityPropertiesPacket() {
    }

    public ServerEntityPropertiesPacket(int entityId, List<Attribute> attributes) {
        this.entityId = entityId;
        this.attributes = attributes;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<Attribute> getAttributes() {
        return this.attributes;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.attributes = new ArrayList<Attribute>();
        int length = in.readInt();
        for (int index = 0; index < length; index++) {
            String key = in.readString();
            double value = in.readDouble();
            List<AttributeModifier> modifiers = new ArrayList<AttributeModifier>();
            int len = in.readVarInt();
            for (int ind = 0; ind < len; ind++) {
                modifiers.add(new AttributeModifier(in.readUUID(), in.readDouble(), mc.minecraft.Magic.key(ModifierOperation.class, in.readByte())));
            }

            this.attributes.add(new Attribute(mc.minecraft.Magic.key(AttributeType.class, key), value, modifiers));
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeInt(this.attributes.size());
        for (Attribute attribute : this.attributes) {
            out.writeString(mc.minecraft.Magic.value(String.class, attribute.getType()));
            out.writeDouble(attribute.getValue());
            out.writeVarInt(attribute.getModifiers().size());
            for (AttributeModifier modifier : attribute.getModifiers()) {
                out.writeUUID(modifier.getUUID());
                out.writeDouble(modifier.getAmount());
                out.writeByte(mc.minecraft.Magic.value(Integer.class, modifier.getOperation()));
            }
        }
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
