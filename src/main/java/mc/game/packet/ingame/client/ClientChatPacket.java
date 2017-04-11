package mc.game.packet.ingame.client;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientChatPacket implements Packet {

    private String message;

    @SuppressWarnings("unused")
    private ClientChatPacket() {
    }

    public ClientChatPacket(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.message = in.readString();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.message);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.game.Util.toString(this);
    }
}
