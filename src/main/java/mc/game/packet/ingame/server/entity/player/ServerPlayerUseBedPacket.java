package mc.game.packet.ingame.server.entity.player;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.metadata.Position;

import java.io.IOException;

public class ServerPlayerUseBedPacket implements Packet {

    private int entityId;
    private Position position;

    @SuppressWarnings("unused")
    private ServerPlayerUseBedPacket() {
    }

    public ServerPlayerUseBedPacket(int entityId, Position position) {
        this.entityId = entityId;
        this.position = position;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Position getPosition() {
        return this.position;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.position = mc.game.Util.readPosition(in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        mc.game.Util.writePosition(out, this.position);
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
