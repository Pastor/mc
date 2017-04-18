package mc.minicraft.component.entity;

import mc.engine.property.PropertyReader;
import mc.minicraft.component.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.api.Sound;
import mc.minicraft.data.game.entity.EntityType;

public final class Oven extends Furniture {

    public Oven(Sound sound, PlayerHandler handler, PropertyReader reader) {
        super(sound, handler, reader, "Oven", EntityType.OVEN);
        col = Color.get(-1, 000, 332, 442);
        sprite = 2;
        xr = 3;
        yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        handler.craftingMenu(player, Crafting.ovenRecipes);
        return true;
    }
}