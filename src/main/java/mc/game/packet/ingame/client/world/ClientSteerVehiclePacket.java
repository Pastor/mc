package mc.game.packet.ingame.client.world;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientSteerVehiclePacket implements Packet {

    private float sideways;
    private float forward;
    private boolean jump;
    private boolean dismount;

    @SuppressWarnings("unused")
    private ClientSteerVehiclePacket() {
    }

    public ClientSteerVehiclePacket(float sideways, float forward, boolean jump, boolean dismount) {
        this.sideways = sideways;
        this.forward = forward;
        this.jump = jump;
        this.dismount = dismount;
    }

    public float getSideways() {
        return this.sideways;
    }

    public float getForward() {
        return this.forward;
    }

    public boolean getJumping() {
        return this.jump;
    }

    public boolean getDismounting() {
        return this.dismount;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.sideways = in.readFloat();
        this.forward = in.readFloat();
        int flags = in.readUnsignedByte();
        this.jump = (flags & 1) > 0;
        this.dismount = (flags & 2) > 0;
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeFloat(this.sideways);
        out.writeFloat(this.forward);
        byte flags = 0;
        if (this.jump) {
            flags = (byte) (flags | 1);
        }

        if (this.dismount) {
            flags = (byte) (flags | 2);
        }

        out.writeByte(flags);
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
