package mc.game.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerEntityAttachPacket implements Packet {

    private int entityId;
    private int attachedToId;

    @SuppressWarnings("unused")
    private ServerEntityAttachPacket() {
    }

    public ServerEntityAttachPacket(int entityId, int attachedToId) {
        this.entityId = entityId;
        this.attachedToId = attachedToId;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getAttachedToId() {
        return this.attachedToId;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readInt();
        this.attachedToId = in.readInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeInt(this.entityId);
        out.writeInt(this.attachedToId);
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
