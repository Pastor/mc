package mc.minicraft.packet.ingame.server.scoreboard;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.scoreboard.ScoreboardPosition;

import java.io.IOException;

public class ServerDisplayScoreboardPacket implements Packet {

    private ScoreboardPosition position;
    private String name;

    @SuppressWarnings("unused")
    private ServerDisplayScoreboardPacket() {
    }

    public ServerDisplayScoreboardPacket(ScoreboardPosition position, String name) {
        this.position = position;
        this.name = name;
    }

    public ScoreboardPosition getPosition() {
        return this.position;
    }

    public String getScoreboardName() {
        return this.name;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.position = mc.minicraft.Magic.key(ScoreboardPosition.class, in.readByte());
        this.name = in.readString();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(mc.minicraft.Magic.value(Integer.class, this.position));
        out.writeString(this.name);
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
