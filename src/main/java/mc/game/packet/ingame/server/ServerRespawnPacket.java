package mc.game.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.player.GameMode;
import mc.game.data.game.setting.Difficulty;
import mc.game.data.game.world.WorldType;

import java.io.IOException;

public class ServerRespawnPacket implements Packet {

    private int dimension;
    private Difficulty difficulty;
    private GameMode gamemode;
    private WorldType worldType;

    @SuppressWarnings("unused")
    private ServerRespawnPacket() {
    }

    public ServerRespawnPacket(int dimension, Difficulty difficulty, GameMode gamemode, WorldType worldType) {
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.gamemode = gamemode;
        this.worldType = worldType;
    }

    public int getDimension() {
        return this.dimension;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public GameMode getGameMode() {
        return this.gamemode;
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.dimension = in.readInt();
        this.difficulty = mc.game.Magic.key(Difficulty.class, in.readUnsignedByte());
        this.gamemode = mc.game.Magic.key(GameMode.class, in.readUnsignedByte());
        this.worldType = mc.game.Magic.key(WorldType.class, in.readString().toLowerCase());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeInt(this.dimension);
        out.writeByte(mc.game.Magic.value(Integer.class, this.difficulty));
        out.writeByte(mc.game.Magic.value(Integer.class, this.gamemode));
        out.writeString(mc.game.Magic.value(String.class, this.worldType));
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
