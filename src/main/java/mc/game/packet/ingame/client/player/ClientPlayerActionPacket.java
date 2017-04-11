package mc.game.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.metadata.Position;
import mc.game.data.game.entity.player.PlayerAction;
import mc.game.data.game.world.block.BlockFace;

import java.io.IOException;

public class ClientPlayerActionPacket implements Packet {

    private PlayerAction action;
    private Position position;
    private BlockFace face;

    @SuppressWarnings("unused")
    private ClientPlayerActionPacket() {
    }

    public ClientPlayerActionPacket(PlayerAction action, Position position, BlockFace face) {
        this.action = action;
        this.position = position;
        this.face = face;
    }

    public PlayerAction getAction() {
        return this.action;
    }

    public Position getPosition() {
        return this.position;
    }

    public BlockFace getFace() {
        return this.face;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.action = mc.game.Magic.key(PlayerAction.class, in.readVarInt());
        this.position = mc.game.Util.readPosition(in);
        this.face = mc.game.Magic.key(BlockFace.class, in.readUnsignedByte());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(mc.game.Magic.value(Integer.class, this.action));
        mc.game.Util.writePosition(out, this.position);
        out.writeByte(mc.game.Magic.value(Integer.class, this.face));
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
