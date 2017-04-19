package mc.minicraft.component.level.tile;

import mc.engine.property.PropertyReader;
import mc.minicraft.component.Screen;
import mc.minicraft.component.entity.*;
import mc.minicraft.component.entity.particle.SmashParticle;
import mc.minicraft.component.entity.particle.TextParticle;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.item.Item;
import mc.minicraft.component.item.ResourceItem;
import mc.minicraft.component.item.ToolItem;
import mc.minicraft.component.item.ToolType;
import mc.minicraft.component.item.resource.Resource;
import mc.minicraft.component.level.Level;

public class HardRockTile extends Tile {
    public HardRockTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        int col = Color.get(334, 334, 223, 223);
        int transitionColor = Color.get(001, 334, 445, level.dirtColor);

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

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }

    public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        hurt(level.playerHandler(), level.propertyReader(), level, x, y, 0);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.pickaxe && tool.level == 4) {
                if (player.payStamina(4 - tool.level)) {
                    hurt(player.handler, player.propertyReader, level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
                    return true;
                }
            }
        }
        return false;
    }

    public void hurt(PlayerHandler handler, PropertyReader reader, Level level, int x, int y, int dmg) {
        int damage = level.getData(x, y) + dmg;
        level.add(new SmashParticle(level.sound, x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(level.sound, "" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)));
        if (damage >= 200) {
            int count = random.nextInt(4) + 1;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level.sound, handler, reader, new ResourceItem(Resource.stone), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
            }
            count = random.nextInt(2);
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level.sound, handler, reader, new ResourceItem(Resource.coal), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
            }
            level.setTile(x, y, Tile.dirt, 0);
        } else {
            level.setData(x, y, damage);
        }
    }

    public void tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) level.setData(xt, yt, damage - 1);
    }
}