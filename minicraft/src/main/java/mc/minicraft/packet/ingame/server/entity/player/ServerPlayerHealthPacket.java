package mc.minicraft.packet.ingame.server.entity.player;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerPlayerHealthPacket implements Packet {

    private float health;
    private int food;
    private float saturation;

    @SuppressWarnings("unused")
    private ServerPlayerHealthPacket() {
    }

    public ServerPlayerHealthPacket(float health, int food, float saturation) {
        this.health = health;
        this.food = food;
        this.saturation = saturation;
    }

    public float getHealth() {
        return this.health;
    }

    public int getFood() {
        return this.food;
    }

    public float getSaturation() {
        return this.saturation;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.health = in.readFloat();
        this.food = in.readVarInt();
        this.saturation = in.readFloat();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeFloat(this.health);
        out.writeVarInt(this.food);
        out.writeFloat(this.saturation);
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
