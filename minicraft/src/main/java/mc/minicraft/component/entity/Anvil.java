package mc.minicraft.component.entity;

import mc.engine.property.PropertyReader;
import mc.minicraft.component.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.api.Sound;
import mc.minicraft.data.game.entity.EntityType;

public class Anvil extends Furniture {
    public Anvil(Sound sound, PlayerHandler handler, PropertyReader reader) {
        super(sound, handler, reader, "Anvil", EntityType.ANVIL);
        col = Color.get(-1, 000, 111, 222);
        sprite = 0;
        xr = 3;
        yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        handler.craftingMenu(player, Crafting.anvilRecipes);
        return true;
    }
}