package mc.minecraft.packet.status.client;


import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.Util;

import java.io.IOException;

public class StatusPingPacket implements Packet {

    private long time;

    @SuppressWarnings("unused")
    private StatusPingPacket() {
    }

    public StatusPingPacket(long time) {
        this.time = time;
    }

    public long getPingTime() {
        return this.time;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.time = in.readLong();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeLong(this.time);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return Util.toString(this);
    }
}
