package mc.game.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerEntityHeadLookPacket implements Packet {

    private int entityId;
    private float headYaw;

    @SuppressWarnings("unused")
    private ServerEntityHeadLookPacket() {
    }

    public ServerEntityHeadLookPacket(int entityId, float headYaw) {
        this.entityId = entityId;
        this.headYaw = headYaw;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public float getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.headYaw = in.readByte() * 360 / 256f;
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeByte((byte) (this.headYaw * 256 / 360));
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
