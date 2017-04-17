package mc.minicraft.packet.ingame.client.window;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientEnchantItemPacket implements Packet {

    private int windowId;
    private int enchantment;

    @SuppressWarnings("unused")
    private ClientEnchantItemPacket() {
    }

    public ClientEnchantItemPacket(int windowId, int enchantment) {
        this.windowId = windowId;
        this.enchantment = enchantment;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getEnchantment() {
        return this.enchantment;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.windowId = in.readByte();
        this.enchantment = in.readByte();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(this.windowId);
        out.writeByte(this.enchantment);
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
