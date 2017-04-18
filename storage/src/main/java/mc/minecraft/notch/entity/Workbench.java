package mc.minicraft.component.entity;

import mc.minecraft.client.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.minecraft.client.screen.CraftingMenu;

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