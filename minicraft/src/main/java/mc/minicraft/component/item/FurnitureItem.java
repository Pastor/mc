package mc.minicraft.component.item;

import mc.minicraft.component.Screen;
import mc.minicraft.component.entity.Furniture;
import mc.minicraft.component.entity.ItemEntity;
import mc.minicraft.component.entity.Player;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.level.Level;
import mc.minicraft.component.level.tile.Tile;

public class FurnitureItem extends Item {
    public Furniture furniture;
    public boolean placed = false;

    public FurnitureItem(Furniture furniture) {
        this.furniture = furniture;
    }

    public int getColor() {
        return furniture.col;
    }

    public int getSprite() {
        return furniture.sprite + 10 * 32;
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
        screen.draw(furniture.name, x + 8, y, Color.get(-1, 555, 555, 555));
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public boolean canAttack() {
        return false;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        if (tile.mayPass(level, xt, yt, furniture)) {
            furniture.x = xt * 16 + 8;
            furniture.y = yt * 16 + 8;
            level.add(furniture);
            placed = true;
            return true;
        }
        return false;
    }

    public boolean isDepleted() {
        return placed;
    }

    public String getName() {
        return furniture.name;
    }
}