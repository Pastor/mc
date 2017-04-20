package mc.minicraft.engine.entity;

import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.crafting.Crafting;
import mc.minicraft.engine.gfx.Color;

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