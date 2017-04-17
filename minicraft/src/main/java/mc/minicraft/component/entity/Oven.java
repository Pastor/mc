package mc.minicraft.component.entity;

import mc.minicraft.component.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.sound.Sound;

public class Oven extends Furniture {
    private final PlayerHandler handler;

    public Oven(Sound sound, PlayerHandler handler) {
        super(sound, "Oven");
        this.handler = handler;
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