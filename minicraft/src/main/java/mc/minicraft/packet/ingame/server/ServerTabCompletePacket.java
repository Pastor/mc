package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerTabCompletePacket implements Packet {

    private String matches[];

    @SuppressWarnings("unused")
    private ServerTabCompletePacket() {
    }

    public ServerTabCompletePacket(String matches[]) {
        this.matches = matches;
    }

    public String[] getMatches() {
        return this.matches;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.matches = new String[in.readVarInt()];
        for (int index = 0; index < this.matches.length; index++) {
            this.matches[index] = in.readString();
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.matches.length);
        for (String match : this.matches) {
            out.writeString(match);
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
