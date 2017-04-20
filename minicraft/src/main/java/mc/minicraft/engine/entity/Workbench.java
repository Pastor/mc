package mc.minicraft.engine.entity;

import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.crafting.Crafting;
import mc.minicraft.engine.gfx.Color;

public final class Workbench extends Furniture {
    public Workbench(Sound sound, PlayerHandler handler, PropertyReader reader) {
        super(sound, handler, reader, "Workbench", EntityType.WORKBENCH);
        col = Color.get(-1, 100, 321, 431);
        sprite = 4;
        xr = 3;
        yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        handler.craftingMenu(player, Crafting.workbenchRecipes);
        return true;
    }
}