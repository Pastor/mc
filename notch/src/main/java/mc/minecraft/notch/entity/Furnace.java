package mc.minecraft.notch.entity;

import mc.minecraft.notch.crafting.Crafting;
import mc.minecraft.notch.gfx.Color;
import mc.minecraft.notch.screen.CraftingMenu;

public class Furnace extends Furniture {
    public Furnace() {
        super("Furnace");
        col = Color.get(-1, 000, 222, 333);
        sprite = 3;
        xr = 3;
        yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.furnaceRecipes, player));
        return true;
    }
}