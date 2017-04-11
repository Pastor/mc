package mc.game.packet.ingame.client.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.Util;
import mc.game.data.game.entity.metadata.Position;

import java.io.IOException;

public class ClientUpdateSignPacket implements Packet {

    private Position position;
    private String lines[];

    @SuppressWarnings("unused")
    private ClientUpdateSignPacket() {
    }

    public ClientUpdateSignPacket(Position position, String lines[]) {
        if (lines.length != 4) {
            throw new IllegalArgumentException("Lines must contain exactly 4 strings!");
        }

        this.position = position;
        this.lines = lines;
    }

    public Position getPosition() {
        return this.position;
    }

    public String[] getLines() {
        return this.lines;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.position = Util.readPosition(in);
        this.lines = new String[4];
        for (int count = 0; count < this.lines.length; count++) {
            this.lines[count] = in.readString();
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        Util.writePosition(out, this.position);
        for (String line : this.lines) {
            out.writeString(line);
        }
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
