package mc.game.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.chunk.Column;
import mc.impl.DefaultBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServerChunkDataPacket implements Packet {
    private Column column;

    @SuppressWarnings("unused")
    private ServerChunkDataPacket() {
    }

    public ServerChunkDataPacket(Column column) {
        this.column = column;
    }

    public Column getColumn() {
        return this.column;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        int x = in.readInt();
        int z = in.readInt();
        boolean fullChunk = in.readBoolean();
        int chunkMask = in.readVarInt();
        byte data[] = in.readBytes(in.readVarInt());
        Object[] tileEntities = new Object[in.readVarInt()];
        for (int i = 0; i < tileEntities.length; i++) {
            tileEntities[i] = mc.game.Util.readNBT(in);
        }

        this.column = mc.game.Util.readColumn(data, x, z, fullChunk, false, chunkMask, tileEntities);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        Buffer.Output netOut = DefaultBuffer.instance().newOutput(byteOut);
        int mask = mc.game.Util.writeColumn(netOut, this.column, this.column.hasBiomeData(), this.column.hasSkylight());

        out.writeInt(this.column.getX());
        out.writeInt(this.column.getZ());
        out.writeBoolean(this.column.hasBiomeData());
        out.writeVarInt(mask);
        out.writeVarInt(byteOut.size());
        out.writeBytes(byteOut.toByteArray(), byteOut.size());
        out.writeVarInt(this.column.getTileEntities().length);
        for (Object tag : this.column.getTileEntities()) {
            mc.game.Util.writeNBT(out, tag);
        }
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
