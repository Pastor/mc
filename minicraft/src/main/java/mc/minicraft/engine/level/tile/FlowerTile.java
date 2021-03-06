package mc.minicraft.engine.level.tile;

import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.ItemEntity;
import mc.minicraft.engine.entity.Mob;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.Item;
import mc.minicraft.engine.item.ResourceItem;
import mc.minicraft.engine.item.ToolItem;
import mc.minicraft.engine.item.ToolType;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.BaseLevel;

public class FlowerTile extends GrassTile {
    public FlowerTile(int id) {
        super(id);
        tiles[id] = this;
        connectsToGrass = true;
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
        super.render(screen, level, x, y);

        int data = level.getData(x, y);
        int shape = (data / 16) % 2;
        int flowerCol = Color.get(10, level.grassColor, 555, 440);

        if (shape == 0) screen.render(x * 16 + 0, y * 16 + 0, 1 + 1 * 32, flowerCol, 0);
        if (shape == 1) screen.render(x * 16 + 8, y * 16 + 0, 1 + 1 * 32, flowerCol, 0);
        if (shape == 1) screen.render(x * 16 + 0, y * 16 + 8, 1 + 1 * 32, flowerCol, 0);
        if (shape == 0) screen.render(x * 16 + 8, y * 16 + 8, 1 + 1 * 32, flowerCol, 0);
    }

    public boolean interact(BaseLevel level, int x, int y, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.add(new ItemEntity(level.sound, player.handler, player.property, new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
                    level.add(new ItemEntity(level.sound, player.handler, player.property, new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
                    level.setTile(x, y, Tile.grass, 0);
                    return true;
                }
            }
        }
        return false;
    }

    public void hurt(BaseLevel level, int x, int y, Mob source, int dmg, int attackDir) {
        int count = random.nextInt(2) + 1;
        for (int i = 0; i < count; i++) {
            level.add(new ItemEntity(level.sound,
                    level.playerHandler(), level.propertyReader(),
                    new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
        }
        level.setTile(x, y, Tile.grass, 0);
    }
}