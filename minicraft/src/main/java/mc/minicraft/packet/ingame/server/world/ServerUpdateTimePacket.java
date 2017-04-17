package mc.minicraft.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerUpdateTimePacket implements Packet {

    private long age;
    private long time;

    @SuppressWarnings("unused")
    private ServerUpdateTimePacket() {
    }

    public ServerUpdateTimePacket(long age, long time) {
        this.age = age;
        this.time = time;
    }

    public long getWorldAge() {
        return this.age;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.age = in.readLong();
        this.time = in.readLong();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeLong(this.age);
        out.writeLong(this.time);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.minicraft.Util.toString(this);
    }
}
