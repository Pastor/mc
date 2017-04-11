package mc.minecraft.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerUnloadChunkPacket implements Packet {
    private int x;
    private int z;

    @SuppressWarnings("unused")
    private ServerUnloadChunkPacket() {
    }

    public ServerUnloadChunkPacket(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.x = in.readInt();
        this.z = in.readInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.z);
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
