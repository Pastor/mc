package mc.minicraft.packet.ingame.server.window;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.window.property.WindowProperty;

import java.io.IOException;

public class ServerWindowPropertyPacket implements Packet {

    private int windowId;
    private int property;
    private int value;

    @SuppressWarnings("unused")
    private ServerWindowPropertyPacket() {
    }

    public ServerWindowPropertyPacket(int windowId, int property, int value) {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

    public <T extends Enum<T> & WindowProperty> ServerWindowPropertyPacket(int windowId, T property, int value) {
        this.windowId = windowId;
        this.property = mc.minicraft.Magic.value(Integer.class, property);
        this.value = value;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getRawProperty() {
        return this.property;
    }

    public <T extends Enum<T> & WindowProperty> T getProperty(Class<T> type) {
        return mc.minicraft.Magic.key(type, this.value);
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.windowId = in.readUnsignedByte();
        this.property = in.readShort();
        this.value = in.readShort();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(this.windowId);
        out.writeShort(this.property);
        out.writeShort(this.value);
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
