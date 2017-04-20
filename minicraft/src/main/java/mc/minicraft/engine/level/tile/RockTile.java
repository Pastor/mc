package mc.minicraft.engine.level.tile;

import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.ItemEntity;
import mc.minicraft.engine.entity.Mob;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.entity.particle.SmashParticle;
import mc.minicraft.engine.entity.particle.TextParticle;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.Item;
import mc.minicraft.engine.item.ResourceItem;
import mc.minicraft.engine.item.ToolItem;
import mc.minicraft.engine.item.ToolType;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.BaseLevel;

public class RockTile extends Tile {
    public RockTile(int id) {
        super(id);
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
        int col = Color.get(444, 444, 333, 333);
        int transitionColor = Color.get(111, 444, 555, level.dirtColor);

        boolean u = level.getTile(x, y - 1) != this;
        boolean d = level.getTile(x, y + 1) != this;
        boolean l = level.getTile(x - 1, y) != this;
        boolean r = level.getTile(x + 1, y) != this;

        boolean ul = level.getTile(x - 1, y - 1) != this;
        boolean dl = level.getTile(x - 1, y + 1) != this;
        boolean ur = level.getTile(x + 1, y - 1) != this;
        boolean dr = level.getTile(x + 1, y + 1) != this;

        if (!u && !l) {
            if (!ul)
                screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);
            else
                screen.render(x * 16 + 0, y * 16 + 0, 7 + 0 * 32, transitionColor, 3);
        } else
            screen.render(x * 16 + 0, y * 16 + 0, (l ? 6 : 5) + (u ? 2 : 1) * 32, transitionColor, 3);

        if (!u && !r) {
            if (!ur)
                screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);
            else
                screen.render(x * 16 + 8, y * 16 + 0, 8 + 0 * 32, transitionColor, 3);
        } else
            screen.render(x * 16 + 8, y * 16 + 0, (r ? 4 : 5) + (u ? 2 : 1) * 32, transitionColor, 3);

        if (!d && !l) {
            if (!dl)
                screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);
            else
                screen.render(x * 16 + 0, y * 16 + 8, 7 + 1 * 32, transitionColor, 3);
        } else
            screen.render(x * 16 + 0, y * 16 + 8, (l ? 6 : 5) + (d ? 0 : 1) * 32, transitionColor, 3);
        if (!d && !r) {
            if (!dr)
                screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
            else
                screen.render(x * 16 + 8, y * 16 + 8, 8 + 1 * 32, transitionColor, 3);
        } else
            screen.render(x * 16 + 8, y * 16 + 8, (r ? 4 : 5) + (d ? 0 : 1) * 32, transitionColor, 3);
    }

    public boolean mayPass(BaseLevel level, int x, int y, Entity e) {
        return false;
    }

    public void hurt(BaseLevel level, int x, int y, Mob source, int dmg, int attackDir) {
        hurt(level, x, y, dmg);
    }

    public boolean interact(BaseLevel level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.pickaxe) {
                if (player.payStamina(4 - tool.level)) {
                    hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
                    return true;
                }
            }
        }
        return false;
    }

    public void hurt(BaseLevel level, int x, int y, int dmg) {
        int damage = level.getData(x, y) + dmg;
        level.add(new SmashParticle(level.sound, x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(level.sound, "" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)));
        if (damage >= 50) {
            int count = random.nextInt(4) + 1;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level.sound, level.playerHandler(), level.propertyReader(),
                        new ResourceItem(Resource.stone), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
            }
            count = random.nextInt(2);
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level.sound, level.playerHandler(), level.propertyReader(),
                        new ResourceItem(Resource.coal), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
            }
            level.setTile(x, y, Tile.dirt, 0);
        } else {
            level.setData(x, y, damage);
        }
    }

    public void tick(BaseLevel level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) level.setData(xt, yt, damage - 1);
    }
}
