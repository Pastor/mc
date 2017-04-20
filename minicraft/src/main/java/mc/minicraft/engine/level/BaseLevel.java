package mc.minicraft.engine.level;

import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.engine.entity.AirWizard;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.entity.PlayerHandler;
import mc.minicraft.engine.level.levelgen.LevelGen;
import mc.minicraft.engine.level.tile.Tile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseLevel {
    protected final Random random = new Random();

    public final UUID id;
    public final int w;
    public final int h;

    public final int grassColor = 141;
    public int dirtColor = 322;
    public final int sandColor = 550;
    public final int depth;
    public int monsterDensity = 8;
    public UUID owner;

    public final Sound sound;
    public final PropertyReader reader;
    public final PlayerHandler handler;

    protected final Map<UUID, EntityData> entities = new ConcurrentHashMap<>();

    protected Comparator<Entity> spriteSorter = (e0, e1) -> {
        if (e1.y < e0.y) return +1;
        if (e1.y > e0.y) return -1;
        return 0;
    };

    protected BaseLevel(Sound sound, PlayerHandler playerHandler, PropertyReader reader,
                        int w, int h, int level, BaseLevel parentLevel) {
        this.id = UUID.randomUUID();
        this.sound = sound;
        this.reader = reader;
        this.handler = playerHandler;
        if (level < 0) {
            dirtColor = 222;
        }
        this.depth = level;
        this.w = w;
        this.h = h;
    }

    public BaseLevel(Sound sound, UUID id, int w, int h, int level, PropertyReader reader, PlayerHandler playerHandler) {
        this.sound = sound;
        this.id = id;
        this.handler = playerHandler;
        this.w = w;
        this.h = h;
        this.depth = level;
        this.reader = reader;
    }

    protected static Set<Entity>[] createEntities(int w, int h) {
        Set<Entity>[] result = new Set[w * h];
        for (int i = 0; i < w * h; i++) {
            result[i] = new HashSet<>();
        }
        return result;
    }

    protected final void fillParentLevel(BaseLevel parentLevel) {
        if (parentLevel != null) {
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++) {
                    if (parentLevel.getTile(x, y) == Tile.stairsDown) {

                        setTile(x, y, Tile.stairsUp, 0);
                        if (depth == 0) {
                            setTile(x - 1, y, Tile.hardRock, 0);
                            setTile(x + 1, y, Tile.hardRock, 0);
                            setTile(x, y - 1, Tile.hardRock, 0);
                            setTile(x, y + 1, Tile.hardRock, 0);
                            setTile(x - 1, y - 1, Tile.hardRock, 0);
                            setTile(x - 1, y + 1, Tile.hardRock, 0);
                            setTile(x + 1, y - 1, Tile.hardRock, 0);
                            setTile(x + 1, y + 1, Tile.hardRock, 0);
                        } else {
                            setTile(x - 1, y, Tile.dirt, 0);
                            setTile(x + 1, y, Tile.dirt, 0);
                            setTile(x, y - 1, Tile.dirt, 0);
                            setTile(x, y + 1, Tile.dirt, 0);
                            setTile(x - 1, y - 1, Tile.dirt, 0);
                            setTile(x - 1, y + 1, Tile.dirt, 0);
                            setTile(x + 1, y - 1, Tile.dirt, 0);
                            setTile(x + 1, y + 1, Tile.dirt, 0);
                        }
                    }

                }
        }
        if (depth == 1) {
            AirWizard aw = new AirWizard(sound);
            aw.x = w * 8;
            aw.y = h * 8;
            add(aw);
        }
    }

    final byte[][] generateMap(int w, int h) {
        byte[][] maps;

        if (depth == 1) {
            dirtColor = 444;
        }
        if (depth == 0)
            maps = LevelGen.createAndValidateTopMap(w, h, 100, 100, 100, 100, 2);
        else if (depth < 0) {
            maps = LevelGen.createAndValidateUndergroundMap(w, h, -depth, 100, 100, 20, 2);
            monsterDensity = 4;
        } else {
            maps = LevelGen.createAndValidateSkyMap(w, h, 2000, 2); // Sky level
            monsterDensity = 4;
        }
        return maps;
    }

    protected final void putEntity(Entity e, int index) {
        entities.put(e.id, new EntityData(index, e));
    }

    protected final EntityData removeEntity(Entity e) {
        return entities.get(e.id);
    }

    public PlayerHandler playerHandler() {
        return handler;
    }

    public PropertyReader propertyReader() {
        return reader;
    }

    public final Player player() {
        EntityData data = entities.get(owner);
        if (data != null)
            return (Player) data.entity;
        return null;
    }

    public final boolean hasPlayer() {
        if (owner != null)
            return entities.containsKey(owner);
        return false;
    }

    public abstract void add(Entity entity);

    public abstract void remove(Entity e);

    public abstract Tile getTile(int x, int y);

    public abstract void setTile(int x, int y, Tile t, int dataVal);

    public abstract int getData(int x, int y);

    public abstract void setData(int x, int y, int val);

    protected abstract void insertEntity(int x, int y, Entity e);

    protected abstract void removeEntity(int x, int y, Entity e);

    public abstract Set<Entity> getEntities(int x0, int y0, int x1, int y1);

    public static final class EntityData {
        public final Integer index;
        public final Entity entity;

        public EntityData(Integer index, Entity entity) {
            this.index = index;
            this.entity = entity;
        }
    }
}
