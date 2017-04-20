package mc.minicraft.engine.entity;

import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.crafting.Crafting;
import mc.minicraft.engine.gfx.Color;

public class Furnace extends Furniture {

    public Furnace(Sound sound, PlayerHandler handler, PropertyReader reader) {
        super(sound, handler, reader, "Furnace", EntityType.FURNACE);
        col = Color.get(-1, 000, 222, 333);
        sprite = 3;
        xr = 3;
        yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        handler.craftingMenu(player, Crafting.furnaceRecipes);
        return true;
    }
}