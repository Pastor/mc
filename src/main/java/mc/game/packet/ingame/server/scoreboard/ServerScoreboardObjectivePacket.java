package mc.game.packet.ingame.server.scoreboard;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.scoreboard.ObjectiveAction;
import mc.game.data.game.scoreboard.ScoreType;

import java.io.IOException;

public class ServerScoreboardObjectivePacket implements Packet {

    private String name;
    private ObjectiveAction action;
    private String displayName;
    private ScoreType type;

    @SuppressWarnings("unused")
    private ServerScoreboardObjectivePacket() {
    }

    public ServerScoreboardObjectivePacket(String name) {
        this.name = name;
        this.action = ObjectiveAction.REMOVE;
    }

    public ServerScoreboardObjectivePacket(String name, ObjectiveAction action, String displayName, ScoreType type) {
        if (action != ObjectiveAction.ADD && action != ObjectiveAction.UPDATE) {
            throw new IllegalArgumentException("(name, action, displayName) constructor only valid for adding and updating objectives.");
        }

        this.name = name;
        this.action = action;
        this.displayName = displayName;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public ObjectiveAction getAction() {
        return this.action;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ScoreType getType() {
        return this.type;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.name = in.readString();
        this.action = mc.game.Magic.key(ObjectiveAction.class, in.readByte());
        if (this.action == ObjectiveAction.ADD || this.action == ObjectiveAction.UPDATE) {
            this.displayName = in.readString();
            this.type = mc.game.Magic.key(ScoreType.class, in.readString());
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.name);
        out.writeByte(mc.game.Magic.value(Integer.class, this.action));
        if (this.action == ObjectiveAction.ADD || this.action == ObjectiveAction.UPDATE) {
            out.writeString(this.displayName);
            out.writeString(mc.game.Magic.value(String.class, this.type));
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
