package mc.minecraft.packet.ingame.server.scoreboard;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.scoreboard.ScoreboardAction;

import java.io.IOException;

public class ServerUpdateScorePacket implements Packet {

    private String entry;
    private ScoreboardAction action;
    private String objective;
    private int value;

    @SuppressWarnings("unused")
    private ServerUpdateScorePacket() {
    }

    public ServerUpdateScorePacket(String entry, String objective) {
        this.entry = entry;
        this.objective = objective;
        this.action = ScoreboardAction.REMOVE;
    }

    public ServerUpdateScorePacket(String entry, String objective, int value) {
        this.entry = entry;
        this.objective = objective;
        this.value = value;
        this.action = ScoreboardAction.ADD_OR_UPDATE;
    }

    public String getEntry() {
        return this.entry;
    }

    public ScoreboardAction getAction() {
        return this.action;
    }

    public String getObjective() {
        return this.objective;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entry = in.readString();
        this.action = mc.minecraft.Magic.key(ScoreboardAction.class, in.readVarInt());
        this.objective = in.readString();
        if (this.action == ScoreboardAction.ADD_OR_UPDATE) {
            this.value = in.readVarInt();
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.entry);
        out.writeVarInt(mc.minecraft.Magic.value(Integer.class, this.action));
        out.writeString(this.objective);
        if (this.action == ScoreboardAction.ADD_OR_UPDATE) {
            out.writeVarInt(this.value);
        }
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
