package mc.minecraft.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.metadata.Position;
import mc.minecraft.data.game.entity.player.BlockBreakStage;

import java.io.IOException;

public class ServerBlockBreakAnimPacket implements Packet {

    private int breakerEntityId;
    private Position position;
    private BlockBreakStage stage;

    @SuppressWarnings("unused")
    private ServerBlockBreakAnimPacket() {
    }

    public ServerBlockBreakAnimPacket(int breakerEntityId, Position position, BlockBreakStage stage) {
        this.breakerEntityId = breakerEntityId;
        this.position = position;
        this.stage = stage;
    }

    public int getBreakerEntityId() {
        return this.breakerEntityId;
    }

    public Position getPosition() {
        return this.position;
    }

    public BlockBreakStage getStage() {
        return this.stage;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.breakerEntityId = in.readVarInt();
        this.position = mc.minecraft.Util.readPosition(in);
        try {
            this.stage = mc.minecraft.Magic.key(BlockBreakStage.class, in.readUnsignedByte());
        } catch (IllegalArgumentException e) {
            this.stage = BlockBreakStage.RESET;
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.breakerEntityId);
        mc.minecraft.Util.writePosition(out, this.position);
        out.writeByte(mc.minecraft.Magic.value(Integer.class, this.stage));
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
