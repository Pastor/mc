package mc.minicraft.engine.level.tile;

import mc.api.Sound;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.ItemEntity;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.Item;
import mc.minicraft.engine.item.ResourceItem;
import mc.minicraft.engine.item.ToolItem;
import mc.minicraft.engine.item.ToolType;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.Level;

public class GrassTile extends Tile {
    public GrassTile(int id) {
        super(id);
        connectsToGrass = true;
    }

    public void render(Screen screen, Level level, int x, int y) {
        int col = Color.get(level.grassColor, level.grassColor, level.grassColor + 111, level.grassColor + 111);
        int transitionColor = Color.get(level.grassColor - 111, level.grassColor, level.grassColor + 111, level.dirtColor);

        boolean u = !level.getTile(x, y - 1).connectsToGrass;
        boolean d = !level.getTile(x, y + 1).connectsToGrass;
        boolean l = !level.getTile(x - 1, y).connectsToGrass;
        boolean r = !level.getTile(x + 1, y).connectsToGrass;

        if (!u && !l) {
            screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);
        } else
            screen.render(x * 16 + 0, y * 16 + 0, (l ? 11 : 12) + (u ? 0 : 1) * 32, transitionColor, 0);

        if (!u && !r) {
            screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);
        } else
            screen.render(x * 16 + 8, y * 16 + 0, (r ? 13 : 12) + (u ? 0 : 1) * 32, transitionColor, 0);

        if (!d && !l) {
            screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);
        } else
            screen.render(x * 16 + 0, y * 16 + 8, (l ? 11 : 12) + (d ? 2 : 1) * 32, transitionColor, 0);
        if (!d && !r) {
            screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
        } else
            screen.render(x * 16 + 8, y * 16 + 8, (r ? 13 : 12) + (d ? 2 : 1) * 32, transitionColor, 0);
    }

    public void tick(Level level, int xt, int yt) {
        if (random.nextInt(40) != 0) return;

        int xn = xt;
        int yn = yt;

        if (random.nextBoolean())
            xn += random.nextInt(2) * 2 - 1;
        else
            yn += random.nextInt(2) * 2 - 1;

        if (level.getTile(xn, yn) == Tile.dirt) {
            level.setTile(xn, yn, this, 0);
        }
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.dirt, 0);
                    level.sound.play(xt, yt, Sound.Type.MONSTER_HURT);
                    if (random.nextInt(5) == 0) {
                        level.add(new ItemEntity(level.sound, player.handler, player.property,
                                new ResourceItem(Resource.seeds), xt * 16 + random.nextInt(10) + 3,
                                yt * 16 + random.nextInt(10) + 3));
                        return true;
                    }
                }
            }
            if (tool.type == ToolType.hoe) {
                if (player.payStamina(4 - tool.level)) {
                    level.sound.play(xt, yt, Sound.Type.MONSTER_HURT);
                    if (random.nextInt(5) == 0) {
                        level.add(new ItemEntity(level.sound, player.handler, player.property,
                                new ResourceItem(Resource.seeds), xt * 16 + random.nextInt(10) + 3,
                                yt * 16 + random.nextInt(10) + 3));
                        return true;
                    }
                    level.setTile(xt, yt, Tile.farmland, 0);
                    return true;
                }
            }
        }
        return false;

    }
}
