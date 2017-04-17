package mc.minicraft.component.level.tile;

import mc.minicraft.component.Screen;
import mc.minicraft.component.entity.ItemEntity;
import mc.minicraft.component.entity.Mob;
import mc.minicraft.component.entity.Player;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.item.Item;
import mc.minicraft.component.item.ResourceItem;
import mc.minicraft.component.item.ToolItem;
import mc.minicraft.component.item.ToolType;
import mc.minicraft.component.item.resource.Resource;
import mc.minicraft.component.level.Level;

public class FlowerTile extends GrassTile {
    public FlowerTile(int id) {
        super(id);
        tiles[id] = this;
        connectsToGrass = true;
    }

    public void render(Screen screen, Level level, int x, int y) {
        super.render(screen, level, x, y);

        int data = level.getData(x, y);
        int shape = (data / 16) % 2;
        int flowerCol = Color.get(10, level.grassColor, 555, 440);

        if (shape == 0) screen.render(x * 16 + 0, y * 16 + 0, 1 + 1 * 32, flowerCol, 0);
        if (shape == 1) screen.render(x * 16 + 8, y * 16 + 0, 1 + 1 * 32, flowerCol, 0);
        if (shape == 1) screen.render(x * 16 + 0, y * 16 + 8, 1 + 1 * 32, flowerCol, 0);
        if (shape == 0) screen.render(x * 16 + 8, y * 16 + 8, 1 + 1 * 32, flowerCol, 0);
    }

    public boolean interact(Level level, int x, int y, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.add(new ItemEntity(level.sound, new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
                    level.add(new ItemEntity(level.sound, new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
                    level.setTile(x, y, Tile.grass, 0);
                    return true;
                }
            }
        }
        return false;
    }

    public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        int count = random.nextInt(2) + 1;
        for (int i = 0; i < count; i++) {
            level.add(new ItemEntity(level.sound, new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
        }
        level.setTile(x, y, Tile.grass, 0);
    }
}