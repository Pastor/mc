package mc.minecraft.client.item;

import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.Furniture;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.gfx.Font;
import mc.minicraft.engine.gfx.Screen;

public class PowerGloveItem extends Item {
    public int getColor() {
        return Color.get(-1, 100, 320, 430);
    }

    public int getSprite() {
        return 7 + 4 * 32;
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
        Font.draw(getName(), screen, x + 8, y, Color.get(-1, 555, 555, 555));
    }

    public String getName() {
        return "Pow glove";
    }

    public boolean interact(Player player, Entity entity, int attackDir) {
        if (entity instanceof Furniture) {
            Furniture f = (Furniture) entity;
            f.take(player);
            return true;
        }
        return false;
    }
}