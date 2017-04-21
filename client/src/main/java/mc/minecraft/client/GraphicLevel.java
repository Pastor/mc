package mc.minecraft.client;

import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.PlayerHandler;
import mc.minicraft.engine.level.BaseLevel;
import mc.minicraft.engine.level.ServerLevel;
import mc.minicraft.engine.level.tile.Tile;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class GraphicLevel extends BaseLevel {

    private final Lock lock = new ReentrantLock();

    private final Buffer[] buffers;

    private byte[] tiles;
    private byte[] data;
    private Set<Entity>[] entitiesInTiles;
    private int swapBuffer = 0;

    final class Buffer {
        final byte[] tiles;
        final byte[] datas;
        final Set<Entity>[] entitiesInTiles;

        private Buffer(int w, int h, byte[] tiles, byte[] data) {
            this.tiles = new byte[tiles.length];
            System.arraycopy(tiles, 0, this.tiles, 0, tiles.length);
            this.datas = new byte[data.length];
            System.arraycopy(data, 0, this.datas, 0, data.length);
            entitiesInTiles = createEntities(w, h);
        }

        void copy(Buffer buffer) {
            System.arraycopy(buffer.tiles, 0, this.tiles, 0, tiles.length);
            System.arraycopy(buffer.datas, 0, this.datas, 0, data.length);
            for (int i = 0; i < buffer.entitiesInTiles.length; i++) {
                this.entitiesInTiles[i] = new HashSet<>(buffer.entitiesInTiles[i]);
            }
        }
    }

    public GraphicLevel(Sound sound, UUID id, int w, int h, int level, byte[] tiles, byte[] data,
                        PropertyReader reader, PlayerHandler playerHandler) {
        super(sound, id, w, h, level, reader, playerHandler);

        buffers = new Buffer[2];
        buffers[0] = new Buffer(w, h, tiles, data);
        buffers[1] = new Buffer(w, h, tiles, data);
        swapBuffer = 0;
        this.tiles = buffers[swapBuffer].tiles;
        this.data = buffers[swapBuffer].datas;
        this.entitiesInTiles = buffers[swapBuffer].entitiesInTiles;
        swapBuffer = 1;
    }

    void updateSwap() {
        int i = prevBuffer();
        buffers[swapBuffer].copy(buffers[i]);
    }

    boolean canRender() {
        return lock.tryLock();
    }

    void unlock() {
        lock.unlock();
    }

    void swap() {
        lock.lock();
        try {
            this.tiles = buffers[swapBuffer].tiles;
            this.data = buffers[swapBuffer].datas;
            this.entitiesInTiles = buffers[swapBuffer].entitiesInTiles;
            nextBuffer();
        } finally {
            lock.unlock();
        }
    }

    private void nextBuffer() {
        swapBuffer++;
        if (swapBuffer >= buffers.length)
            swapBuffer = 0;
    }

    private int prevBuffer() {
        return swapBuffer - 1 < 0 ? buffers.length - 1 : swapBuffer - 1;
    }

    void updateTile(int x, int y, Tile t, int dataVal) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        buffers[swapBuffer].tiles[x + y * w] = t.id;
        updateData(x, y, dataVal);
    }

    void updateData(int x, int y, int value) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        buffers[swapBuffer].datas[x + y * w] = (byte) value;
    }

    void updateRemoveEntity(Entity entity) {
        int x = entity.x >> 4;
        int y = entity.y >> 4;
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        EntityData data = removeEntity(entity);
        if (data != null) {
            buffers[swapBuffer].entitiesInTiles[data.index].remove(entity);
        }
    }

    void updateInsertEntity(Entity entity) {
        entity.removed = false;
        entity.init(this);

        int x = entity.x >> 4;
        int y = entity.y >> 4;

        if (x < 0 || y < 0 || x >= w || y >= h)
            return;
        int index = x + y * w;
        putEntity(entity, index);
        buffers[swapBuffer].entitiesInTiles[index].add(entity);
    }

    void renderBackground(Screen screen, int xScroll, int yScroll) {
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

    void renderSprites(Screen screen, int xScroll, int yScroll) {
        final List<Entity> rowSprites = new ArrayList<>();
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.w() + 15) >> 4;
        int h = (screen.h() + 15) >> 4;

        screen.setOffset(xScroll, yScroll);
        for (int y = yo; y <= h + yo; y++) {
            for (int x = xo; x <= w + xo; x++) {
                if (x < 0 || y < 0 || x >= this.w || y >= this.h)
                    continue;
                rowSprites.addAll(entitiesInTiles[x + y * this.w]);
            }
            if (rowSprites.size() > 0) {
                sortAndRender(screen, rowSprites);
            }
            rowSprites.clear();
        }
        screen.setOffset(0, 0);
    }

    void renderLight(Screen screen, int xScroll, int yScroll) {
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
                if (lr > 0)
                    screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8);
            }
        }
        screen.setOffset(0, 0);
    }

    private void sortAndRender(Screen screen, List<Entity> list) {
        list.sort(spriteSorter);
        for (Entity aList : list) {
            aList.render(screen);
        }
    }


    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock;
        return Tile.tiles[tiles[x + y * w]];
    }

    public void setTile(int x, int y, Tile t, int dataVal) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        tiles[x + y * w] = t.id;
        setData(x, y, dataVal);
    }

    public int getData(int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h) return 0;
        return data[x + y * w] & 0xff;
    }

    public void setData(int x, int y, int val) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        data[x + y * w] = (byte) val;
    }

    void update(Entity entity) {
        lock.lock();
        try {
            entity.removed = false;
            entity.init(this);
            final EntityData data = readEntity(entity.id);
            int index = (entity.x >> 4) + (entity.y >> 4) * w;
            if (data != null) {
                entitiesInTiles[data.index].remove(entity);
            }
            entitiesInTiles[index].add(entity);
            putEntity(entity, index);
        } finally {
            lock.unlock();
        }
    }

    public void add(Entity entity) {
        entity.removed = false;
        entity.init(this);
        insertEntity(entity.x >> 4, entity.y >> 4, entity);
    }

    public void remove(Entity e) {
        int xto = e.x >> 4;
        int yto = e.y >> 4;
        removeEntity(xto, yto, e);
    }

    public void insertEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= w || y >= h)
            return;
        int index = x + y * w;
        putEntity(e, index);
        entitiesInTiles[index].add(e);
    }

    public void removeEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        int i = x + y * w;
        EntityData data = removeEntity(e);
        if (data != null) {
            entitiesInTiles[data.index].remove(e);
        }
    }

    @Override
    public Set<Entity> getEntities(int x0, int y0, int x1, int y1) {
        return ServerLevel.getEntities(entitiesInTiles, w, h, x0, y0, x1, y1);
    }
}
