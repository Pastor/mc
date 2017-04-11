package mc.minecraft.packet.ingame.client;


import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientPluginMessagePacket implements Packet {

    private String channel;
    private byte data[];

    @SuppressWarnings("unused")
    private ClientPluginMessagePacket() {
    }

    public ClientPluginMessagePacket(String channel, byte data[]) {
        this.channel = channel;
        this.data = data;
    }

    public String getChannel() {
        return this.channel;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.channel = in.readString();
        this.data = in.readBytes(in.available());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.channel);
        out.writeBytes(this.data);
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
