package mc.minicraft.component.entity;

import mc.engine.property.PropertyReader;
import mc.minicraft.component.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.api.Sound;
import mc.minicraft.data.game.entity.EntityType;

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