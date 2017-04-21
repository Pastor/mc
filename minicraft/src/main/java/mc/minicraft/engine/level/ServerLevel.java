package mc.minicraft.engine.level;

import mc.api.Session;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.ServerPlayer;
import mc.minicraft.engine.LevelHandler;
import mc.minicraft.engine.entity.*;
import mc.minicraft.engine.level.tile.Tile;
import mc.minicraft.packet.ingame.server.ServerSoundEffectPacket;
import mc.minicraft.packet.ingame.server.level.ServerUpdateLevelPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ServerLevel extends BaseLevel {
    public final byte[] tiles;
    public final byte[] data;
    private final Set<Entity>[] entitiesInTiles;
    public final LevelCollector handler;

    @SuppressWarnings("unchecked")
    public ServerLevel(Sound sound, PlayerHandler playerHandler, PropertyReader reader,
                       int w, int h, int level, BaseLevel parentLevel) {
        super(sound, playerHandler, reader, w, h, level, parentLevel);
        this.handler = new LevelCollector(w, h, this);
        entitiesInTiles = createEntities(w, h);
        byte[][] maps = generateMap(w, h);
        tiles = maps[0];
        data = maps[1];
        fillParentLevel(parentLevel);
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock;
        return Tile.tiles[tiles[x + y * w]];
    }

    public void setTile(int x, int y, Tile t, int dataVal) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        tiles[x + y * w] = t.id;
        data[x + y * w] = (byte) dataVal;
        handler.setTile(x, y, t.id);
        handler.setData(x, y, dataVal);
    }

    public int getData(int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h) return 0;
        return data[x + y * w] & 0xff;
    }

    public synchronized void setData(int x, int y, int val) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        data[x + y * w] = (byte) val;
        handler.setData(x, y, val);
    }

    public void add(Entity entity) {
        entity.removed = false;
        entity.init(this);
        insertEntity(entity.x >> 4, entity.y >> 4, entity);
    }

    public void remove(Entity e) {
        removeEntity(e);
        int xto = e.x >> 4;
        int yto = e.y >> 4;
        removeEntity(xto, yto, e);
    }

    protected void insertEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= w || y >= h)
            return;
        int index = x + y * w;
        putEntity(e, index);
        entitiesInTiles[index].add(e);
        handler.insertEntity(x, y, e);
    }

    protected void removeEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        int i = x + y * w;
        entitiesInTiles[i].remove(e);
        handler.removeEntity(x, y, e);
        EntityData data = removeEntity(e);
        if (data != null) {
            entitiesInTiles[data.index].remove(e);
        }
    }

    public void trySpawn(int count) {
        for (int i = 0; i < count; i++) {
            final Mob mob;

            int minLevel = 1;
            int maxLevel = 1;
            if (depth < 0) {
                maxLevel = (-depth) + 1;
            }
            if (depth > 0) {
                minLevel = maxLevel = 4;
            }

            int lvl = random.nextInt(maxLevel - minLevel + 1) + minLevel;
            if (random.nextInt(2) == 0)
                mob = new Slime(sound, lvl);
            else
                mob = new Zombie(sound, lvl);
            if (mob.findStartPos(this)) {
                this.add(mob);
            }
        }
    }

    public void tick() {
        if (Tile.tickCount % 1000 == 0)
            trySpawn(1);

        for (int i = 0; i < w * h / 50; i++) {
            int xt = random.nextInt(w);
            int yt = random.nextInt(w);
            getTile(xt, yt).tick(this, xt, yt);
        }
        synchronized (entities) {
            for (EntityData e : new HashMap<>(entities).values()) {
                int xto = e.entity.x >> 4;
                int yto = e.entity.y >> 4;
                int rxo = e.entity.x;
                int ryo = e.entity.y;

                e.entity.tick();
                if (e.entity.removed) {
                    entities.remove(e.entity.id);
                    removeEntity(xto, yto, e.entity);
                    if (e.entity instanceof Player && ((Player) e.entity).die) {
                        //
                    }
                } else {
                    int xt = e.entity.x >> 4;
                    int yt = e.entity.y >> 4;
                    int rx = e.entity.x;
                    int ry = e.entity.y;

                    if (xto != xt || yto != yt) {
                        removeEntity(xto, yto, e.entity);
                        insertEntity(xt, yt, e.entity);
                    } else if ((rx != rxo || ry != ryo)) {
                        removeEntity(xto, yto, e.entity);
                        insertEntity(xt, yt, e.entity);
                    }
                }
            }
        }
    }

    public Set<Entity> getEntities(int x0, int y0, int x1, int y1) {
        return getEntities(entitiesInTiles, w, h, x0, y0, x1, y1);
    }

    public static Set<Entity> getEntities(Set<Entity>[] entitiesInTiles, int w, int h, int x0, int y0, int x1, int y1) {
        Set<Entity> result = new HashSet<>();
        int xt0 = (x0 >> 4) - 1;
        int yt0 = (y0 >> 4) - 1;
        int xt1 = (x1 >> 4) + 1;
        int yt1 = (y1 >> 4) + 1;
        for (int y = yt0; y <= yt1; y++) {
            for (int x = xt0; x <= xt1; x++) {
                if (x < 0 || y < 0 || x >= w || y >= h) continue;
                Set<Entity> entities = entitiesInTiles[x + y * w];
                for (Entity e : entities) {
                    if (e.intersects(x0, y0, x1, y1)) result.add(e);
                }
            }
        }
        return result;
    }

    public void process(Session session, ServerPlayer player) {
        handler.process(session, player);
    }

    public void resetState() {
        handler.reset();
    }

    private static final class LevelCollector implements LevelHandler {

        private final Set<DataKey> datas = new HashSet<>();
        private final Set<DataKey> tiles = new HashSet<>();

        private final int w;
        private final int h;
        private final Set<Entity>[] insertEntities;
        private final Set<Entity>[] removeEntities;
        private final Set<Sound.Type> sounds = new HashSet<>();

        LevelCollector(int w, int h, BaseLevel level) {
            this.w = w;
            this.h = h;
            insertEntities = new Set[w * h];
            for (int i = 0; i < w * h; i++) {
                insertEntities[i] = new HashSet<>();
            }

            removeEntities = new Set[w * h];
            for (int i = 0; i < w * h; i++) {
                removeEntities[i] = new HashSet<>();
            }
        }

        public void reset() {
            Stream.of(insertEntities).forEach(Set::clear);
            Stream.of(removeEntities).forEach(Set::clear);
            datas.clear();
            tiles.clear();
            sounds.clear();
        }

        public void process(Session session, ServerPlayer player) {
            onViewport(w, h, player, (xStart1, yStart1, xEnd1, yEnd1) -> {
                Set<Entity> insertEntities1 = ServerLevel.getEntities(LevelCollector.this.insertEntities, w, h,
                        xStart1, yStart1, xEnd1, yEnd1);
                Set<Entity> removeEntities1 = ServerLevel.getEntities(LevelCollector.this.removeEntities, w, h,
                        xStart1, yStart1, xEnd1, yEnd1);
                ServerUpdateLevelPacket packet = new ServerUpdateLevelPacket();
                packet.insertEntities.addAll(insertEntities1);
                packet.removeEntities.addAll(removeEntities1);
                packet.datas.addAll(datas);
                packet.tiles.addAll(tiles);

                if (insertEntities1.contains(player.player)) {
                    player.player.updated = false;
                }
                try {
                    if (datas.size() != 0 || tiles.size() != 0 || insertEntities1.size() != 0 || removeEntities1.size() != 0) {
                        session.send(packet);
                    }
                    if (sounds.size() > 0) {
                        for (Sound.Type type : sounds) {
                            ServerSoundEffectPacket effect = new ServerSoundEffectPacket();
                            effect.type = type;
                            session.send(effect);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        @Override
        public void setData(int x, int y, int data) {
            datas.add(new DataKey(x, y, data));
        }

        @Override
        public void setTile(int x, int y, int tile) {
            tiles.add(new DataKey(x, y, tile));
        }

        @Override
        public void insertEntity(int x, int y, Entity entity) {
            insertEntities[x + y * w].add(entity);
        }

        @Override
        public void removeEntity(int x, int y, Entity entity) {
            removeEntities[x + y * w].add(entity);
        }

        @Override
        public void sound(int x, int y, Sound.Type type) {
            sounds.add(type);
        }
    }

    private static void onViewport(int w, int h, ServerPlayer player, Viewport viewport) {
        int distance = player.visibleDistance;
        int xStart = player.player.x - (distance << 4);
        if (xStart < 0)
            xStart = 0;
        int yStart = player.player.y - (distance << 4);
        if (yStart < 0)
            yStart = 0;
        int xEnd = player.player.x + (distance << 4);
        int yEnd = player.player.y + (distance << 4);
        viewport.onRect(xStart, yStart, xEnd, yEnd);
    }

    private interface Viewport {
        void onRect(int xStart, int yStart, int xEnd, int yEnd);
    }
}
