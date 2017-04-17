package mc.minicraft.component.entity;

import mc.minicraft.component.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.sound.Sound;

public class Furnace extends Furniture {
    private final PlayerHandler handler;

    public Furnace(Sound sound, PlayerHandler handler) {
        super(sound, "Furnace");
        this.handler = handler;
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