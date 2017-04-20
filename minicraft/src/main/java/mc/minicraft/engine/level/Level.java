package mc.minicraft.engine.level;

import mc.api.Session;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.ServerPlayer;
import mc.minicraft.engine.LevelHandler;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.*;
import mc.minicraft.engine.level.levelgen.LevelGen;
import mc.minicraft.engine.level.tile.Tile;
import mc.minicraft.packet.ingame.server.ServerSoundEffectPacket;
import mc.minicraft.packet.ingame.server.level.ServerUpdateLevelPacket;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Stream;

public final class Level {
    private final Random random = new Random();

    public final UUID id;
    public final int w;
    public final int h;

    public final byte[] tiles;
    public final byte[] data;
    public final Set<Entity>[] entitiesInTiles;

    public int grassColor = 141;
    public int dirtColor = 322;
    public int sandColor = 550;
    public final int depth;
    public int monsterDensity = 8;
    public UUID owner;
    private final Map<UUID, Integer> entitiesIndex = new HashMap<>();

    private final Map<UUID, Entity> entities = new HashMap<>();
    private Comparator<Entity> spriteSorter = (e0, e1) -> {
        if (e1.y < e0.y) return +1;
        if (e1.y > e0.y) return -1;
        return 0;
    };

    public final Sound sound;
    public final LevelCollector handler;
    public final PropertyReader reader;
    public final PlayerHandler playerHandler;

    @SuppressWarnings("unchecked")
    public Level(Sound sound, PlayerHandler playerHandler, PropertyReader reader,
                 int w, int h, int level, Level parentLevel) {
        this.id = UUID.randomUUID();
        this.sound = sound;
        this.reader = reader;
        this.playerHandler = playerHandler;
        this.handler = new LevelCollector(w, h, this);
        if (level < 0) {
            dirtColor = 222;
        }
        this.depth = level;
        this.w = w;
        this.h = h;
        byte[][] maps;

        if (level == 1) {
            dirtColor = 444;
        }
        if (level == 0)
            maps = LevelGen.createAndValidateTopMap(w, h, 100, 100, 100, 100, 2);
//            maps = LevelGen.createAndValidateTopMap(w, h, 1, 1, 1, 1, 2);
        else if (level < 0) {
            maps = LevelGen.createAndValidateUndergroundMap(w, h, -level, 100, 100, 20, 2);
//            maps = LevelGen.createAndValidateUndergroundMap(w, h, -level, 1, 1, 1, 2);
            monsterDensity = 4;
        } else {
            maps = LevelGen.createAndValidateSkyMap(w, h, 2000, 2); // Sky level
//            maps = LevelGen.createAndValidateSkyMap(w, h, 20, 2); // Sky level
            monsterDensity = 4;
        }

        tiles = maps[0];
        data = maps[1];

        if (parentLevel != null) {
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++) {
                    if (parentLevel.getTile(x, y) == Tile.stairsDown) {

                        setTile(x, y, Tile.stairsUp, 0);
                        if (level == 0) {
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

        entitiesInTiles = new Set[w * h];
        for (int i = 0; i < w * h; i++) {
            entitiesInTiles[i] = new HashSet<>();
        }

        if (level == 1) {
            AirWizard aw = new AirWizard(sound);
            aw.x = w * 8;
            aw.y = h * 8;
            add(aw);
        }
    }

    public Level(Sound sound, UUID id, int w, int h, int level, byte[] tiles, byte[] data, PropertyReader reader, PlayerHandler playerHandler) {
        this.sound = sound;
        this.id = id;
        this.handler = new LevelCollector(w, h, this);
        this.playerHandler = playerHandler;
        this.w = w;
        this.h = h;
        this.tiles = tiles;
        this.data = data;
        this.depth = level;
        this.reader = reader;
        entitiesInTiles = new Set[w * h];
        for (int i = 0; i < w * h; i++) {
            entitiesInTiles[i] = new HashSet<>();
        }
    }

    public void renderBackground(Screen screen, int xScroll, int yScroll) {
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.w() + 15) >> 4;
        int h = (screen.h() + 15) >> 4;
        screen.setOffset(xScroll, yScroll);
        for (int y = yo; y <= h + yo; y++) {
            for (int x = xo; x <= w + xo; x++) {
                getTile(x, y).render(screen, this, x, y);
            }
        }
        screen.setOffset(0, 0);
    }


    public Player player() {
        return (Player) entities.get(owner);
    }

    public boolean hasPlayer() {
        return entities.containsKey(owner);
    }

    public synchronized void renderSprites(Screen screen, int xScroll, int yScroll) {
        final List<Entity> rowSprites = new ArrayList<>();
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.w() + 15) >> 4;
        int h = (screen.h() + 15) >> 4;

        screen.setOffset(xScroll, yScroll);
        for (int y = yo; y <= h + yo; y++) {
            for (int x = xo; x <= w + xo; x++) {
                if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue;
                rowSprites.addAll(entitiesInTiles[x + y * this.w]);
            }
            if (rowSprites.size() > 0) {
                sortAndRender(screen, rowSprites);
            }
            rowSprites.clear();
        }
        screen.setOffset(0, 0);
    }

    public synchronized void renderLight(Screen screen, int xScroll, int yScroll) {
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.w() + 15) >> 4;
        int h = (screen.h() + 15) >> 4;

        screen.setOffset(xScroll, yScroll);
        int r = 4;
        for (int y = yo - r; y <= h + yo + r; y++) {
            for (int x = xo - r; x <= w + xo + r; x++) {
                if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue;
                Set<Entity> entities = entitiesInTiles[x + y * this.w];
                for (Entity e : entities) {
                    // e.render(screen);
                    int lr = e.getLightRadius();
                    if (lr > 0)
                        screen.renderLight(e.x - 1, e.y - 4, lr * 8);
                }
                int lr = getTile(x, y).getLightRadius(this, x, y);
                if (lr > 0) screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8);
            }
        }
        screen.setOffset(0, 0);
    }

    // private void renderLight(Screen screen, int x, int y, int r) {
    // screen.renderLight(x, y, r);
    // }

    private void sortAndRender(Screen screen, List<Entity> list) {
        Collections.sort(list, spriteSorter);
        for (Entity aList : list) {
            aList.render(screen);
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock;
        return Tile.tiles[tiles[x + y * w]];
    }

    public synchronized void setTile(int x, int y, Tile t, int dataVal) {
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

    public synchronized void add(Entity entity) {
        entity.removed = false;
        synchronized (entities) {
            entities.put(entity.id, entity);
        }
        entity.init(this);
        insertEntity(entity.x >> 4, entity.y >> 4, entity);
    }

    public synchronized void remove(Entity e) {
        synchronized (entities) {
            entities.remove(e.id);
        }
        int xto = e.x >> 4;
        int yto = e.y >> 4;
        removeEntity(xto, yto, e);
    }

    protected void insertEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= w || y >= h)
            return;
        int index = x + y * w;
        entitiesIndex.put(e.id, index);
        entitiesInTiles[index].add(e);
        handler.insertEntity(x, y, e);
    }

    protected void removeEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        int i = x + y * w;
        entitiesInTiles[i].remove(e);
        handler.removeEntity(x, y, e);
        Integer index = entitiesIndex.get(e.id);
        if (index != null) {
            entitiesInTiles[index].remove(e);
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
        trySpawn(1);

        for (int i = 0; i < w * h / 50; i++) {
            int xt = random.nextInt(w);
            int yt = random.nextInt(w);
            getTile(xt, yt).tick(this, xt, yt);
        }
        synchronized (entities) {
            for (Entity e : new HashMap<>(entities).values()) {
                int xto = e.x >> 4;
                int yto = e.y >> 4;
                int rxo = e.x;
                int ryo = e.y;

                e.tick();
                if (e.removed) {
                    entities.remove(e.id);
                    removeEntity(xto, yto, e);
                    if (e instanceof Player && ((Player) e).die) {
                        //
                    }
                } else {
                    int xt = e.x >> 4;
                    int yt = e.y >> 4;
                    int rx = e.x;
                    int ry = e.y;

                    if (xto != xt || yto != yt) {
                        removeEntity(xto, yto, e);
                        insertEntity(xt, yt, e);
                    } else if ((rx != rxo || ry != ryo)) {
                        removeEntity(xto, yto, e);
                        insertEntity(xt, yt, e);
                    }
                }
            }
        }
    }

    public synchronized Set<Entity> getEntities(int x0, int y0, int x1, int y1) {
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

    public static final class LevelCollector implements LevelHandler {

        private final Set<DataKey> datas = new HashSet<>();
        private final Set<DataKey> tiles = new HashSet<>();

        private final int w;
        private final int h;
        private final Set<Entity>[] insertEntities;
        private final Set<Entity>[] removeEntities;
        private final Set<Sound.Type> sounds = new HashSet<>();

        LevelCollector(int w, int h, Level level) {
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
                Set<Entity> insertEntities1 = Level.getEntities(LevelCollector.this.insertEntities, w, h,
                        xStart1, yStart1, xEnd1, yEnd1);
                Set<Entity> removeEntities1 = Level.getEntities(LevelCollector.this.removeEntities, w, h,
                        xStart1, yStart1, xEnd1, yEnd1);
                ServerUpdateLevelPacket packet = new ServerUpdateLevelPacket();
                packet.insertEntities.addAll(insertEntities1);
                packet.removeEntities.addAll(removeEntities1);
                packet.datas.addAll(datas);
                packet.tiles.addAll(tiles);
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

    public PlayerHandler playerHandler() {
        return playerHandler;
    }

    public PropertyReader propertyReader() {
        return reader;
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