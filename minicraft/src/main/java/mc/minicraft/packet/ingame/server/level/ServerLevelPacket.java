package mc.minicraft.packet.ingame.server.level;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.component.level.Level;
import mc.minicraft.component.sound.Sound;

import java.io.IOException;

public final class ServerLevelPacket implements Packet {
    private int w;
    private int h;
    private int level;

    private byte[] tiles;
    private byte[] data;

    @SuppressWarnings("unused")
    public ServerLevelPacket() {
    }

    private ServerLevelPacket(int w, int h, int level, byte[] tiles, byte[] data) {
        this.w = w;
        this.h = h;
        this.level = level;
        this.tiles = tiles;
        this.data = data;
    }

    public ServerLevelPacket(Level level) {
        this(level.w, level.h, level.depth, level.tiles, level.data);
    }

    public Level readLevel(Sound sound) {
        return new Level(sound, w, h, level, tiles, data);
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        w = in.readVarInt();
        h = in.readVarInt();
        level = in.readVarInt();
        tiles = in.readBytes(in.readVarInt());
        data = in.readBytes(in.readVarInt());
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
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
