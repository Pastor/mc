package mc.minicraft.packet.ingame.server.entity.spawn;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.entity.metadata.Position;
import mc.minicraft.data.game.entity.type.PaintingType;
import mc.minicraft.data.game.entity.type.object.HangingDirection;

import java.io.IOException;
import java.util.UUID;

public class ServerSpawnPaintingPacket implements Packet {

    private int entityId;
    private UUID uuid;
    private PaintingType paintingType;
    private Position position;
    private HangingDirection direction;

    @SuppressWarnings("unused")
    private ServerSpawnPaintingPacket() {
    }

    public ServerSpawnPaintingPacket(int entityId, UUID uuid, PaintingType paintingType, Position position, HangingDirection direction) {
        this.entityId = entityId;
        this.uuid = uuid;
        this.paintingType = paintingType;
        this.position = position;
        this.direction = direction;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public PaintingType getPaintingType() {
        return this.paintingType;
    }

    public Position getPosition() {
        return this.position;
    }

    public HangingDirection getDirection() {
        return this.direction;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.uuid = in.readUUID();
        this.paintingType = mc.minicraft.Magic.key(PaintingType.class, in.readString());
        this.position = mc.minicraft.Util.readPosition(in);
        this.direction = mc.minicraft.Magic.key(HangingDirection.class, in.readUnsignedByte());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeUUID(this.uuid);
        out.writeString(mc.minicraft.Magic.value(String.class, this.paintingType));
        mc.minicraft.Util.writePosition(out, this.position);
        out.writeByte(mc.minicraft.Magic.value(Integer.class, this.direction));
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
