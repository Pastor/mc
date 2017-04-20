package mc.minicraft.engine.level.tile;

import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.Mob;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.item.Item;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.BaseLevel;

import java.util.Random;

public class Tile {
    public static int tickCount = 0;
    protected Random random = new Random();

    public static final Tile[] tiles = new Tile[256];
    public static final Tile grass = new GrassTile(0);
    public static final Tile rock = new RockTile(1);
    public static final Tile water = new WaterTile(2);
    public static final Tile flower = new FlowerTile(3);
    public static final Tile tree = new TreeTile(4);
    public static final Tile dirt = new DirtTile(5);
    public static final Tile sand = new SandTile(6);
    public static final Tile cactus = new CactusTile(7);
    public static final Tile hole = new HoleTile(8);
    public static final Tile treeSapling = new SaplingTile(9, grass, tree);
    public static final Tile cactusSapling = new SaplingTile(10, sand, cactus);
    public static final Tile farmland = new FarmTile(11);
    public static final Tile wheat = new WheatTile(12);
    public static final Tile lava = new LavaTile(13);
    public static final Tile stairsDown = new StairsTile(14, false);
    public static final Tile stairsUp = new StairsTile(15, true);
    public static final Tile infiniteFall = new InfiniteFallTile(16);
    public static final Tile cloud = new CloudTile(17);
    public static final Tile hardRock = new HardRockTile(18);
    public static final Tile ironOre = new OreTile(19, Resource.ironOre);
    public static final Tile goldOre = new OreTile(20, Resource.goldOre);
    public static final Tile gemOre = new OreTile(21, Resource.gem);
    public static final Tile cloudCactus = new CloudCactusTile(22);

    public final byte id;

    public boolean connectsToGrass = false;
    public boolean connectsToSand = false;
    public boolean connectsToLava = false;
    public boolean connectsToWater = false;

    public Tile(int id) {
        this.id = (byte) id;
        if (tiles[id] != null)
            throw new RuntimeException("Duplicate tile ids!");
        tiles[id] = this;
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
    }

    public boolean mayPass(BaseLevel level, int x, int y, Entity e) {
        return true;
    }

    public int getLightRadius(BaseLevel level, int x, int y) {
        return 0;
    }

    public void hurt(BaseLevel level, int x, int y, Mob source, int dmg, int attackDir) {
    }

    public void bumpedInto(BaseLevel level, int xt, int yt, Entity entity) {
    }

    public void tick(BaseLevel level, int xt, int yt) {
    }

    public void steppedOn(BaseLevel level, int xt, int yt, Entity entity) {
    }

    public boolean interact(BaseLevel level, int xt, int yt, Player player, Item item, int attackDir) {
        return false;
    }

    public boolean use(BaseLevel level, int xt, int yt, Player player, int attackDir) {
        return false;
    }

    public boolean connectsToLiquid() {
        return connectsToWater || connectsToLava;
    }
}