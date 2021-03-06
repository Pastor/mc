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
import mc.minicraft.engine.level.BaseLevel;

public class DirtTile extends Tile {
    public DirtTile(int id) {
        super(id);
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
        int col = Color.get(level.dirtColor, level.dirtColor, level.dirtColor - 111, level.dirtColor - 111);
        screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);
        screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);
        screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);
        screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
    }

    public boolean interact(BaseLevel level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.hole, 0);
                    level.add(new ItemEntity(level.sound, level.playerHandler(), level.propertyReader(),
                            new ResourceItem(Resource.dirt),
                            xt * 16 + random.nextInt(10) + 3,
                            yt * 16 + random.nextInt(10) + 3));
                    level.sound.play(xt, yt, Sound.Type.MONSTER_HURT);
                    return true;
                }
            }
            if (tool.type == ToolType.hoe) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.farmland, 0);
                    level.sound.play(xt, yt, Sound.Type.MONSTER_HURT);
                    return true;
                }
            }
        }
        return false;
    }
}
