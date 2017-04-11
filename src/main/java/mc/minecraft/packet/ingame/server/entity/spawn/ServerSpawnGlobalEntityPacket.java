package mc.minecraft.packet.ingame.server.entity.spawn;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.type.GlobalEntityType;

import java.io.IOException;

public class ServerSpawnGlobalEntityPacket implements Packet {

    private int entityId;
    private GlobalEntityType type;
    private double x;
    private double y;
    private double z;

    @SuppressWarnings("unused")
    private ServerSpawnGlobalEntityPacket() {
    }

    public ServerSpawnGlobalEntityPacket(int entityId, GlobalEntityType type, double x, double y, double z) {
        this.entityId = entityId;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public GlobalEntityType getType() {
        return this.type;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.type = mc.minecraft.Magic.key(GlobalEntityType.class, in.readByte());
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeByte(mc.minecraft.Magic.value(Integer.class, this.type));
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
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
