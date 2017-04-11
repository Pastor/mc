package mc.game.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.metadata.Position;
import mc.game.data.game.world.block.UpdatedTileType;

import java.io.IOException;

public class ServerUpdateTileEntityPacket implements Packet {

    private Position position;
    private UpdatedTileType type;
    private Object nbt;

    @SuppressWarnings("unused")
    private ServerUpdateTileEntityPacket() {
    }

    public ServerUpdateTileEntityPacket(Position position, UpdatedTileType type, Object nbt) {
        this.position = position;
        this.type = type;
        this.nbt = nbt;
    }

    public Position getPosition() {
        return this.position;
    }

    public UpdatedTileType getType() {
        return this.type;
    }

    public Object getNBT() {
        return this.nbt;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.position = mc.game.Util.readPosition(in);
        this.type = mc.game.Magic.key(UpdatedTileType.class, in.readUnsignedByte());
        this.nbt = mc.game.Util.readNBT(in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        mc.game.Util.writePosition(out, this.position);
        out.writeByte(mc.game.Magic.value(Integer.class, this.type));
        mc.game.Util.writeNBT(out, this.nbt);
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
