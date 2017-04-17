package mc.minicraft.packet.ingame.client;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.Util;
import mc.minicraft.data.game.entity.metadata.Position;

import java.io.IOException;

public class ClientTabCompletePacket implements Packet {

    private String text;
    private boolean assumeCommand;
    private Position lookingAt;

    @SuppressWarnings("unused")
    private ClientTabCompletePacket() {
    }

    public ClientTabCompletePacket(String text, boolean assumeCommand) {
        this(text, assumeCommand, null);
    }

    public ClientTabCompletePacket(String text, boolean assumeCommand, Position lookingAt) {
        this.text = text;
        this.assumeCommand = assumeCommand;
        this.lookingAt = lookingAt;
    }

    public String getText() {
        return this.text;
    }

    public boolean getAssumeCommand() {
        return this.assumeCommand;
    }

    public Position getLookingAt() {
        return this.lookingAt;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.text = in.readString();
        this.assumeCommand = in.readBoolean();
        this.lookingAt = in.readBoolean() ? Util.readPosition(in) : null;
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.text);
        out.writeBoolean(this.assumeCommand);
        out.writeBoolean(this.lookingAt != null);
        if (this.lookingAt != null) {
            Util.writePosition(out, this.lookingAt);
        }
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
