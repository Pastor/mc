package mc.minicraft.engine.item;

import mc.minicraft.data.game.entity.ItemType;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.Furniture;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.gfx.Color;

public final class PowerGloveItem extends Item {
    public PowerGloveItem() {
        super(ItemType.POWER_GLOVE);
        sprite = 7 + 4 * 32;
        color = Color.get(-1, 100, 320, 430);
        name = "Pow glove";
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
        screen.draw(getName(), x + 8, y, Color.get(-1, 555, 555, 555));
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