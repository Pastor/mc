package mc.minecraft.client;

import mc.api.Sound;
import mc.engine.ConcurrentObject;
import mc.engine.property.PropertyReader;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.PlayerHandler;
import mc.minicraft.engine.level.BaseLevel;
import mc.minicraft.engine.level.ServerLevel;
import mc.minicraft.engine.level.tile.Tile;

import java.util.*;

public final class GraphicLevel extends BaseLevel {

    private final ConcurrentObject<byte[]> tiles;
    private final ConcurrentObject<byte[]> data;
    private final ConcurrentObject<Set<Entity>[]> entitiesInTiles;

    public GraphicLevel(Sound sound, UUID id, int w, int h, int level, byte[] tiles, byte[] data,
                        PropertyReader reader, PlayerHandler playerHandler) {
        super(sound, id, w, h, level, reader, playerHandler);
        this.tiles = new ConcurrentObject<>(tiles);
        this.data = new ConcurrentObject<>(data);
        this.entitiesInTiles = new ConcurrentObject<>(createEntities(w, h));
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


    /**
     * TODO: Опимизировать
     */
    public void renderSprites(Screen screen, int xScroll, int yScroll) {
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
                int finalX = x;
                int finalY = y;
                rowSprites.addAll(entitiesInTiles.read(entitiesInTiles -> entitiesInTiles[finalX + finalY * this.w]));
            }
            if (rowSprites.size() > 0) {
                sortAndRender(screen, rowSprites);
            }
            rowSprites.clear();
        }
        screen.setOffset(0, 0);
    }

    /**
     * TODO: Опимизировать
     */
    public void renderLight(Screen screen, int xScroll, int yScroll) {
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.w() + 15) >> 4;
        int h = (screen.h() + 15) >> 4;

        screen.setOffset(xScroll, yScroll);
        int r = 4;
        for (int y = yo - r; y <= h + yo + r; y++) {
            for (int x = xo - r; x <= w + xo + r; x++) {
                if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue;
                int finalX = x;
                int finalY = y;
                Set<Entity> entities = new HashSet<>(
                        entitiesInTiles.read(entitiesInTiles -> entitiesInTiles[finalX + finalY * this.w]));
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
        Collections.sort(list, spriteSorter);
        for (Entity aList : list) {
            aList.render(screen);
        }
    }


    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock;
        return tiles.read(tiles -> Tile.tiles[tiles[x + y * w]]);
    }

    public void setTile(int x, int y, Tile t, int dataVal) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        tiles.write(tiles -> tiles[x + y * w] = t.id);
        setData(x, y, dataVal);
    }

    public int getData(int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h) return 0;
        return data.read(data -> data[x + y * w] & 0xff);
    }

    public void setData(int x, int y, int val) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        data.write(data -> data[x + y * w] = (byte) val);
    }

    void update(Entity entity) {
        entity.removed = false;
        entity.init(this);
        final EntityData data = readEntity(entity.id);
        int index = (entity.x >> 4) + (entity.y >> 4) * w;
        entitiesInTiles.write(entitiesInTiles -> {
            if (data != null) {
                entitiesInTiles[data.index].remove(entity);
            }
            entitiesInTiles[index].remove(entity);
            entitiesInTiles[index].add(entity);
            putEntity(entity, index);
            return null;
        });
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
        entitiesInTiles.write(entitiesInTiles -> entitiesInTiles[index].add(e));
    }

    public void removeEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= w || y >= h) return;
        int i = x + y * w;
        EntityData data = removeEntity(e);
        if (data != null) {
            entitiesInTiles.write(entitiesInTiles -> entitiesInTiles[data.index].remove(e));
        }
    }

    @Override
    public Set<Entity> getEntities(int x0, int y0, int x1, int y1) {
        return entitiesInTiles.read(entitiesInTiles -> ServerLevel.getEntities(entitiesInTiles, w, h, x0, y0, x1, y1));
    }
}
