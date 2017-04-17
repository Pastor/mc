package mc.minicraft.component.entity;

import mc.minicraft.component.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.sound.Sound;

public class Anvil extends Furniture {
    private final PlayerHandler handler;

    public Anvil(Sound sound, PlayerHandler handler) {
        super(sound, "Anvil");
        this.handler = handler;
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