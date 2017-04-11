package mc.minecraft.packet.ingame.client.window;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientCloseWindowPacket implements Packet {

    private int windowId;

    @SuppressWarnings("unused")
    private ClientCloseWindowPacket() {
    }

    public ClientCloseWindowPacket(int windowId) {
        this.windowId = windowId;
    }

    public int getWindowId() {
        return this.windowId;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.windowId = in.readByte();
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
        return mc.minecraft.Util.toString(this);
    }
}
