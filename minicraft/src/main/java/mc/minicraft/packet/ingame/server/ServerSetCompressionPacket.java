package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerSetCompressionPacket implements Packet {
    private int threshold;

    @SuppressWarnings("unused")
    private ServerSetCompressionPacket() {
    }

    public ServerSetCompressionPacket(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return this.threshold;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.threshold = in.readVarInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.threshold);
    }

    @Override
    public boolean isPriority() {
        return true;
    }

    @Override
    public String toString() {
        return mc.minicraft.Util.toString(this);
    }
}
