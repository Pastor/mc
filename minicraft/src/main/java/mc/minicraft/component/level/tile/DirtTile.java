package mc.minicraft.component.level.tile;

import mc.minicraft.component.Screen;
import mc.minicraft.component.entity.ItemEntity;
import mc.minicraft.component.entity.Player;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.item.Item;
import mc.minicraft.component.item.ResourceItem;
import mc.minicraft.component.item.ToolItem;
import mc.minicraft.component.item.ToolType;
import mc.minicraft.component.item.resource.Resource;
import mc.minicraft.component.level.Level;
import mc.api.Sound;

public class DirtTile extends Tile {
    public DirtTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        int col = Color.get(level.dirtColor, level.dirtColor, level.dirtColor - 111, level.dirtColor - 111);
        screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);
        screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);
        screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);
        screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.hole, 0);
                    level.add(new ItemEntity(level.sound,
                            new ResourceItem(Resource.dirt),
                            xt * 16 + random.nextInt(10) + 3,
                            yt * 16 + random.nextInt(10) + 3));
                    level.sound.play(Sound.Type.MONSTER_HURT);
                    return true;
                }
            }
            if (tool.type == ToolType.hoe) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.farmland, 0);
                    level.sound.play(Sound.Type.MONSTER_HURT);
                    return true;
                }
            }
        }
        return false;
    }
}
