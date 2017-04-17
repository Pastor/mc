package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.entity.player.GameMode;
import mc.minicraft.data.game.setting.Difficulty;
import mc.minicraft.data.game.world.WorldType;

import java.io.IOException;

public class ServerJoinGamePacket implements Packet {

    private int entityId;
    private boolean hardcore;
    private GameMode gamemode;
    private int dimension;
    private Difficulty difficulty;
    private int maxPlayers;
    private WorldType worldType;
    private boolean reducedDebugInfo;

    @SuppressWarnings("unused")
    private ServerJoinGamePacket() {
    }

    public ServerJoinGamePacket(int entityId, boolean hardcore, GameMode gamemode, int dimension, Difficulty difficulty, int maxPlayers, WorldType worldType, boolean reducedDebugInfo) {
        this.entityId = entityId;
        this.hardcore = hardcore;
        this.gamemode = gamemode;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.worldType = worldType;
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public boolean getHardcore() {
        return this.hardcore;
    }

    public GameMode getGameMode() {
        return this.gamemode;
    }

    public int getDimension() {
        return this.dimension;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    public boolean getReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readInt();
        int gamemode = in.readUnsignedByte();
        this.hardcore = (gamemode & 8) == 8;
        gamemode &= -9;
        this.gamemode = mc.minicraft.Magic.key(GameMode.class, gamemode);
        this.dimension = in.readInt();
        this.difficulty = mc.minicraft.Magic.key(Difficulty.class, in.readUnsignedByte());
        this.maxPlayers = in.readUnsignedByte();
        this.worldType = mc.minicraft.Magic.key(WorldType.class, in.readString().toLowerCase());
        this.reducedDebugInfo = in.readBoolean();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeInt(this.entityId);
        int gamemode = mc.minicraft.Magic.value(Integer.class, this.gamemode);
        if (this.hardcore) {
            gamemode |= 8;
        }

        out.writeByte(gamemode);
        out.writeInt(this.dimension);
        out.writeByte(mc.minicraft.Magic.value(Integer.class, this.difficulty));
        out.writeByte(this.maxPlayers);
        out.writeString(mc.minicraft.Magic.value(String.class, this.worldType));
        out.writeBoolean(this.reducedDebugInfo);
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
