package mc.game.packet.login.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.Util;
import mc.game.data.message.Message;

import java.io.IOException;

public class LoginDisconnectPacket implements Packet {

    private Message message;

    @SuppressWarnings("unused")
    private LoginDisconnectPacket() {
    }

    public LoginDisconnectPacket(String text) {
        this(Message.fromString(text));
    }

    public LoginDisconnectPacket(Message message) {
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
        return Util.toString(this);
    }
}
