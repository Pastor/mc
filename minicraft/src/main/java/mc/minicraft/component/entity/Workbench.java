package mc.minicraft.component.entity;

import mc.minicraft.component.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.sound.Sound;

public class Workbench extends Furniture {
    private final PlayerHandler handler;

    public Workbench(Sound sound, PlayerHandler handler) {
        super(sound, "Workbench");
        this.handler = handler;
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