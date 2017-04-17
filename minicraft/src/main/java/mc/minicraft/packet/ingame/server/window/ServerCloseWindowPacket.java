package mc.minicraft.packet.ingame.server.window;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerCloseWindowPacket implements Packet {

    private int windowId;

    @SuppressWarnings("unused")
    private ServerCloseWindowPacket() {
    }

    public ServerCloseWindowPacket(int windowId) {
        this.windowId = windowId;
    }

    public int getWindowId() {
        return this.windowId;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.windowId = in.readUnsignedByte();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(this.windowId);
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
