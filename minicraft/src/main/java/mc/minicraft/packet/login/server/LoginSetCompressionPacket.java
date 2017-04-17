package mc.minicraft.packet.login.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.Util;

import java.io.IOException;

public class LoginSetCompressionPacket implements Packet {
    private int threshold;

    @SuppressWarnings("unused")
    private LoginSetCompressionPacket() {
    }

    public LoginSetCompressionPacket(int threshold) {
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
        return Util.toString(this);
    }
}
