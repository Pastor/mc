package mc.minicraft.engine.level.tile;

import mc.engine.property.PropertyReader;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.*;
import mc.minicraft.engine.entity.particle.SmashParticle;
import mc.minicraft.engine.entity.particle.TextParticle;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.Item;
import mc.minicraft.engine.item.ResourceItem;
import mc.minicraft.engine.item.ToolItem;
import mc.minicraft.engine.item.ToolType;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.BaseLevel;

public class TreeTile extends Tile {
    public TreeTile(int id) {
        super(id);
        connectsToGrass = true;
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
        int col = Color.get(10, 30, 151, level.grassColor);
        int barkCol1 = Color.get(10, 30, 430, level.grassColor);
        int barkCol2 = Color.get(10, 30, 320, level.grassColor);

        boolean u = level.getTile(x, y - 1) == this;
        boolean l = level.getTile(x - 1, y) == this;
        boolean r = level.getTile(x + 1, y) == this;
        boolean d = level.getTile(x, y + 1) == this;
        boolean ul = level.getTile(x - 1, y - 1) == this;
        boolean ur = level.getTile(x + 1, y - 1) == this;
        boolean dl = level.getTile(x - 1, y + 1) == this;
        boolean dr = level.getTile(x + 1, y + 1) == this;

        if (u && ul && l) {
            screen.render(x * 16 + 0, y * 16 + 0, 10 + 1 * 32, col, 0);
        } else {
            screen.render(x * 16 + 0, y * 16 + 0, 9 + 0 * 32, col, 0);
        }
        if (u && ur && r) {
            screen.render(x * 16 + 8, y * 16 + 0, 10 + 2 * 32, barkCol2, 0);
        } else {
            screen.render(x * 16 + 8, y * 16 + 0, 10 + 0 * 32, col, 0);
        }
        if (d && dl && l) {
            screen.render(x * 16 + 0, y * 16 + 8, 10 + 2 * 32, barkCol2, 0);
        } else {
            screen.render(x * 16 + 0, y * 16 + 8, 9 + 1 * 32, barkCol1, 0);
        }
        if (d && dr && r) {
            screen.render(x * 16 + 8, y * 16 + 8, 10 + 1 * 32, col, 0);
        } else {
            screen.render(x * 16 + 8, y * 16 + 8, 10 + 3 * 32, barkCol2, 0);
        }
    }

    public void tick(BaseLevel level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) level.setData(xt, yt, damage - 1);
    }

    public boolean mayPass(BaseLevel level, int x, int y, Entity e) {
        return false;
    }

    public void hurt(BaseLevel level, int x, int y, Mob source, int dmg, int attackDir) {
        hurt(level.playerHandler(), level.propertyReader(), level, x, y, dmg);
    }

    public boolean interact(BaseLevel level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.axe) {
                if (player.payStamina(4 - tool.level)) {
                    hurt(player.handler, player.property, level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
                    return true;
                }
            }
        }
        return false;
    }

    private void hurt(PlayerHandler handler, PropertyReader reader, BaseLevel level, int x, int y, int dmg) {
        {
            int count = random.nextInt(10) == 0 ? 1 : 0;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level.sound, handler, reader,
                        new ResourceItem(Resource.apple), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
            }
        }
        int damage = level.getData(x, y) + dmg;
        level.add(new SmashParticle(level.sound, x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(level.sound, "" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)));
        if (damage >= 20) {
            int count = random.nextInt(2) + 1;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level.sound, handler, reader,
                        new ResourceItem(Resource.wood), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
            }
            count = random.nextInt(random.nextInt(4) + 1);
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level.sound, handler, reader,
                        new ResourceItem(Resource.acorn), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
            }
            level.setTile(x, y, grass, 0);
        } else {
            level.setData(x, y, damage);
        }
    }
}
