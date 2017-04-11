package mc.game.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.EntityStatus;

import java.io.IOException;

public class ServerEntityStatusPacket implements Packet {

    protected int entityId;
    protected EntityStatus status;

    @SuppressWarnings("unused")
    private ServerEntityStatusPacket() {
    }

    public ServerEntityStatusPacket(int entityId, EntityStatus status) {
        this.entityId = entityId;
        this.status = status;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public EntityStatus getStatus() {
        return this.status;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readInt();
        this.status = mc.game.Magic.key(EntityStatus.class, in.readByte());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeInt(this.entityId);
        out.writeByte(mc.game.Magic.value(Integer.class, this.status));
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
