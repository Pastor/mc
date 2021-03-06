package mc.minicraft.packet.ingame.server.level;

import mc.api.Buffer;
import mc.api.Packet;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.engine.LevelHandler;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.PlayerHandler;
import mc.minicraft.packet.ingame.server.GraphicsUpdatePacket;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public final class ServerUpdateLevelPacket extends GraphicsUpdatePacket {

    public final List<LevelHandler.DataKey> tiles = new LinkedList<>();
    public final List<LevelHandler.DataKey> datas = new LinkedList<>();

    public final List<Entity> insertEntities = new LinkedList<>();
    public final List<Entity> removeEntities = new LinkedList<>();

    @Override
    public void read(Buffer.Input in) throws IOException {
        tiles.clear();
        datas.clear();
        insertEntities.clear();
        removeEntities.clear();
        int tileLength = in.readVarInt();
        for (int i = 0; i < tileLength; ++i) {
            tiles.add(new LevelHandler.DataKey(in.readVarInt(), in.readVarInt(), in.readVarInt()));
        }
        int dataLength = in.readVarInt();
        for (int i = 0; i < dataLength; ++i) {
            datas.add(new LevelHandler.DataKey(in.readVarInt(), in.readVarInt(), in.readVarInt()));
        }
        int insertLength = in.readVarInt();
        for (int i = 0; i < insertLength; ++i) {
            insertEntities.add(Entity.readEntity(sound, handler, property, in));
        }
        int removeLength = in.readVarInt();
        for (int i = 0; i < removeLength; ++i) {
            removeEntities.add(Entity.readEntity(sound, handler, property, in));
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(tiles.size());
        for (LevelHandler.DataKey key : tiles) {
            out.writeVarInt(key.x);
            out.writeVarInt(key.y);
            out.writeVarInt(key.value);
        }
        out.writeVarInt(datas.size());
        for (LevelHandler.DataKey key : datas) {
            out.writeVarInt(key.x);
            out.writeVarInt(key.y);
            out.writeVarInt(key.value);
        }
        out.writeVarInt(insertEntities.size());
        for (Entity entity : insertEntities) {
            entity.write(out);
        }
        out.writeVarInt(removeEntities.size());
        for (Entity entity : removeEntities) {
            entity.write(out);
        }
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return "ServerUpdateLevelPacket{" +
                "tiles=" + tiles +
                ", datas=" + datas +
                ", insertEntities=" + insertEntities +
                ", removeEntities=" + removeEntities +
                '}';
    }
}
