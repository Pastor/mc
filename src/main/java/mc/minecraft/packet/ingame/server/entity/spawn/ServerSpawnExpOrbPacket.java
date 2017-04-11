package mc.minecraft.packet.ingame.server.entity.spawn;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerSpawnExpOrbPacket implements Packet {

    private int entityId;
    private double x;
    private double y;
    private double z;
    private int exp;

    @SuppressWarnings("unused")
    private ServerSpawnExpOrbPacket() {
    }

    public ServerSpawnExpOrbPacket(int entityId, double x, double y, double z, int exp) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.exp = exp;
    }

    public int getEntityId() {
        return this.entityId;
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

    public int getExp() {
        return this.exp;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.exp = in.readShort();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeShort(this.exp);
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