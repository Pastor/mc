package mc.minecraft.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerVehicleMovePacket implements Packet {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    @SuppressWarnings("unused")
    private ServerVehicleMovePacket() {
    }

    public ServerVehicleMovePacket(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
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