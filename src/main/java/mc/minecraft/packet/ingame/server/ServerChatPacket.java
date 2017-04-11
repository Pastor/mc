package mc.minecraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.MessageType;
import mc.minecraft.data.message.Message;

import java.io.IOException;

public class ServerChatPacket implements Packet {

    private Message message;
    private MessageType type;

    @SuppressWarnings("unused")
    private ServerChatPacket() {
    }

    public ServerChatPacket(String text) {
        this(Message.fromString(text));
    }

    public ServerChatPacket(Message message) {
        this(message, MessageType.SYSTEM);
    }

    public ServerChatPacket(String text, MessageType type) {
        this(Message.fromString(text), type);
    }

    public ServerChatPacket(Message message, MessageType type) {
        this.message = message;
        this.type = type;
    }

    public Message getMessage() {
        return this.message;
    }

    public MessageType getType() {
        return this.type;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.message = Message.fromString(in.readString());
        this.type = mc.minecraft.Magic.key(MessageType.class, in.readByte());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.message.toJsonString());
        out.writeByte(mc.minecraft.Magic.value(Integer.class, this.type));
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