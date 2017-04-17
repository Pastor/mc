package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.setting.Difficulty;

import java.io.IOException;

public class ServerDifficultyPacket implements Packet {

    private Difficulty difficulty;

    @SuppressWarnings("unused")
    private ServerDifficultyPacket() {
    }

    public ServerDifficultyPacket(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.difficulty = mc.minicraft.Magic.key(Difficulty.class, in.readUnsignedByte());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(mc.minicraft.Magic.value(Integer.class, this.difficulty));
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
