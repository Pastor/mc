package mc.minicraft.engine.entity;

import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.crafting.Crafting;
import mc.minicraft.engine.gfx.Color;

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