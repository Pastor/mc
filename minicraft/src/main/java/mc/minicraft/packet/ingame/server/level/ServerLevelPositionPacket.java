package mc.minicraft.packet.ingame.server.level;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.ServerPlayer;
import mc.minicraft.engine.level.ServerLevel;

import java.io.IOException;
import java.util.UUID;

public abstract class ServerLevelPositionPacket implements Packet {
    public int w;
    public int h;
    public int level;

    public byte[] tiles;
    public byte[] data;

    public int xPlayer;
    public int yPlayer;
    public UUID ownerId;
    public UUID levelId;

    @SuppressWarnings({"unused", "WeakerAccess"})
    public ServerLevelPositionPacket() {
    }

    private ServerLevelPositionPacket(int w, int h, int level, byte[] tiles, byte[] data) {
        this.w = w;
        this.h = h;
        this.level = level;
        this.tiles = tiles;
        this.data = data;
    }

    ServerLevelPositionPacket(ServerLevel level, ServerPlayer player) {
        this(level.w, level.h, level.depth, level.tiles, level.data);
        this.xPlayer = player.player.x;
        this.yPlayer = player.player.y;
        this.ownerId = player.player.id;
        this.levelId = level.id;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        w = in.readVarInt();
        h = in.readVarInt();
        level = in.readVarInt();
        tiles = in.readBytes(in.readVarInt());
        data = in.readBytes(in.readVarInt());
        xPlayer = in.readVarInt();
        yPlayer = in.readVarInt();
        ownerId = UUID.fromString(in.readString());
        levelId = UUID.fromString(in.readString());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(w);
        out.writeVarInt(h);
        out.writeVarInt(level);
        out.writeVarInt(tiles.length);
        out.writeBytes(tiles);
        out.writeVarInt(data.length);
        out.writeBytes(data);
        out.writeVarInt(xPlayer);
        out.writeVarInt(yPlayer);
        out.writeString(ownerId.toString());
        out.writeString(levelId.toString());
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
