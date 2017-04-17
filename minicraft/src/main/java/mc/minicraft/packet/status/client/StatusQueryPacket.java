package mc.minicraft.packet.status.client;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.Util;

import java.io.IOException;

public class StatusQueryPacket implements Packet {

    public StatusQueryPacket() {
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
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
