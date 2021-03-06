package mc.minicraft.engine.level.tile;

import mc.engine.property.PropertyReader;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.*;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.Item;
import mc.minicraft.engine.item.ResourceItem;
import mc.minicraft.engine.item.ToolItem;
import mc.minicraft.engine.item.ToolType;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.BaseLevel;

public class WheatTile extends Tile {
    public WheatTile(int id) {
        super(id);
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
        int age = level.getData(x, y);
        int col = Color.get(level.dirtColor - 121, level.dirtColor - 11, level.dirtColor, 50);
        int icon = age / 10;
        if (icon >= 3) {
            col = Color.get(level.dirtColor - 121, level.dirtColor - 11, 50 + (icon) * 100, 40 + (icon - 3) * 2 * 100);
            if (age == 50) {
                col = Color.get(0, 0, 50 + (icon) * 100, 40 + (icon - 3) * 2 * 100);
            }
            icon = 3;
        }

        screen.render(x * 16 + 0, y * 16 + 0, 4 + 3 * 32 + icon, col, 0);
        screen.render(x * 16 + 8, y * 16 + 0, 4 + 3 * 32 + icon, col, 0);
        screen.render(x * 16 + 0, y * 16 + 8, 4 + 3 * 32 + icon, col, 1);
        screen.render(x * 16 + 8, y * 16 + 8, 4 + 3 * 32 + icon, col, 1);
    }

    public void tick(BaseLevel level, int xt, int yt) {
        if (random.nextInt(2) == 0) return;

        int age = level.getData(xt, yt);
        if (age < 50) level.setData(xt, yt, age + 1);
    }

    public boolean interact(BaseLevel level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, dirt, 0);
                    return true;
                }
            }
        }
        return false;
    }

    public void steppedOn(BaseLevel level, int xt, int yt, Entity entity) {
        if (random.nextInt(60) != 0) return;
        if (level.getData(xt, yt) < 2) return;
        harvest(level.playerHandler(), level.propertyReader(), level, xt, yt);
    }

    public void hurt(BaseLevel level, int x, int y, Mob source, int dmg, int attackDir) {

        harvest(level.playerHandler(), level.propertyReader(), level, x, y);
    }

    private void harvest(PlayerHandler handler, PropertyReader reader, BaseLevel level, int x, int y) {
        int age = level.getData(x, y);

        int count = random.nextInt(2);
        for (int i = 0; i < count; i++) {
            level.add(new ItemEntity(level.sound, handler, reader, new ResourceItem(Resource.seeds), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
        }

        count = 0;
        if (age == 50) {
            count = random.nextInt(3) + 2;
        } else if (age >= 40) {
            count = random.nextInt(2) + 1;
        }
        for (int i = 0; i < count; i++) {
            level.add(new ItemEntity(level.sound, handler, reader, new ResourceItem(Resource.wheat), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
        }

        level.setTile(x, y, dirt, 0);
    }
}
