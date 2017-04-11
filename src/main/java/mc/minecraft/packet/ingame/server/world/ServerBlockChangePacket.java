package mc.minecraft.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.world.block.BlockChangeRecord;

import java.io.IOException;

public class ServerBlockChangePacket implements Packet {

    private BlockChangeRecord record;

    @SuppressWarnings("unused")
    private ServerBlockChangePacket() {
    }

    public ServerBlockChangePacket(BlockChangeRecord record) {
        this.record = record;
    }

    public BlockChangeRecord getRecord() {
        return this.record;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.record = new BlockChangeRecord(mc.minecraft.Util.readPosition(in), mc.minecraft.Util.readBlockState(in));
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        mc.minecraft.Util.writePosition(out, this.record.getPosition());
        mc.minecraft.Util.writeBlockState(out, this.record.getBlock());
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
