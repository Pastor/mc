package mc.game.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.message.Message;

import java.io.IOException;

public class ServerPlayerListDataPacket implements Packet {
    private Message header;
    private Message footer;

    @SuppressWarnings("unused")
    private ServerPlayerListDataPacket() {
    }

    public ServerPlayerListDataPacket(Message header, Message footer) {
        this.header = header;
        this.footer = footer;
    }

    public Message getHeader() {
        return this.header;
    }

    public Message getFooter() {
        return this.footer;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.header = Message.fromString(in.readString());
        this.footer = Message.fromString(in.readString());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.header.toJsonString());
        out.writeString(this.footer.toJsonString());
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
