package mc.minicraft.packet.ingame.server;


import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.message.Message;

import java.io.IOException;

public class ServerDisconnectPacket implements Packet {

    private Message message;

    @SuppressWarnings("unused")
    private ServerDisconnectPacket() {
    }

    public ServerDisconnectPacket(String text) {
        this(Message.fromString(text));
    }

    public ServerDisconnectPacket(Message message) {
        this.message = message;
    }

    public Message getReason() {
        return this.message;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.message = Message.fromString(in.readString());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.message.toJsonString());
    }

    @Override
    public boolean isPriority() {
        return true;
    }

    @Override
    public String toString() {
        return mc.minicraft.Util.toString(this);
    }
}
