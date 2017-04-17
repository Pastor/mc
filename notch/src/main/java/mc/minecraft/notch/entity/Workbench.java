package mc.minecraft.notch.entity;


import mc.minecraft.notch.crafting.Crafting;
import mc.minecraft.notch.gfx.Color;
import mc.minecraft.notch.screen.CraftingMenu;

public class Workbench extends Furniture {
    public Workbench() {
        super("Workbench");
        col = Color.get(-1, 100, 321, 431);
        sprite = 4;
        xr = 3;
        yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.workbenchRecipes, player));
        return true;
    }
}