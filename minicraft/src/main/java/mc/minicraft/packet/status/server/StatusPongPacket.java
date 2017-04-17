package mc.minicraft.packet.status.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.Util;

import java.io.IOException;

public class StatusPongPacket implements Packet {

    private long time;

    @SuppressWarnings("unused")
    private StatusPongPacket() {
    }

    public StatusPongPacket(long time) {
        this.time = time;
    }

    public long getPingTime() {
        return this.time;
    }

    public void read(Buffer.Input in) throws IOException {
        this.time = in.readLong();
    }

    public void write(Buffer.Output out) throws IOException {
        out.writeLong(this.time);
    }

    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return Util.toString(this);
    }
}
