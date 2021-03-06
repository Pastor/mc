package mc.minicraft.engine.level.tile;

import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.ItemEntity;
import mc.minicraft.engine.entity.Mob;
import mc.minicraft.engine.entity.particle.SmashParticle;
import mc.minicraft.engine.entity.particle.TextParticle;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.ResourceItem;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.BaseLevel;

public class CactusTile extends Tile {
    public CactusTile(int id) {
        super(id);
        connectsToSand = true;
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
        int col = Color.get(20, 40, 50, level.sandColor);
        screen.render(x * 16 + 0, y * 16 + 0, 8 + 2 * 32, col, 0);
        screen.render(x * 16 + 8, y * 16 + 0, 9 + 2 * 32, col, 0);
        screen.render(x * 16 + 0, y * 16 + 8, 8 + 3 * 32, col, 0);
        screen.render(x * 16 + 8, y * 16 + 8, 9 + 3 * 32, col, 0);
    }

    public boolean mayPass(BaseLevel level, int x, int y, Entity e) {
        return false;
    }

    public void hurt(BaseLevel level, int x, int y, Mob source, int dmg, int attackDir) {
        int damage = level.getData(x, y) + dmg;
        level.add(new SmashParticle(level.sound, x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(level.sound, "" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)));
        if (damage >= 10) {
            int count = random.nextInt(2) + 1;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level.sound, level.playerHandler(), level.propertyReader(),
                        new ResourceItem(Resource.cactusFlower),
                        x * 16 + random.nextInt(10) + 3,
                        y * 16 + random.nextInt(10) + 3));
            }
            level.setTile(x, y, Tile.sand, 0);
        } else {
            level.setData(x, y, damage);
        }
    }

    public void bumpedInto(BaseLevel level, int x, int y, Entity entity) {
        entity.hurt(this, x, y, 1);
    }

    public void tick(BaseLevel level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) level.setData(xt, yt, damage - 1);
    }
}