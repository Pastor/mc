package mc.game.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.world.block.BlockChangeRecord;

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
        this.record = new BlockChangeRecord(mc.game.Util.readPosition(in), mc.game.Util.readBlockState(in));
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        mc.game.Util.writePosition(out, this.record.getPosition());
        mc.game.Util.writeBlockState(out, this.record.getBlock());
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
