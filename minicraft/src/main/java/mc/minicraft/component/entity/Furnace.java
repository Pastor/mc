package mc.minicraft.component.entity;

import mc.engine.property.PropertyReader;
import mc.minicraft.component.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.api.Sound;
import mc.minicraft.data.game.entity.EntityType;

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