package mc.minecraft.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.metadata.EntityMetadata;

import java.io.IOException;

public class ServerEntityMetadataPacket implements Packet {

    private int entityId;
    private EntityMetadata metadata[];

    @SuppressWarnings("unused")
    private ServerEntityMetadataPacket() {
    }

    public ServerEntityMetadataPacket(int entityId, EntityMetadata metadata[]) {
        this.entityId = entityId;
        this.metadata = metadata;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public EntityMetadata[] getMetadata() {
        return this.metadata;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.metadata = mc.minecraft.Util.readEntityMetadata(in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        mc.minecraft.Util.writeEntityMetadata(out, this.metadata);
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