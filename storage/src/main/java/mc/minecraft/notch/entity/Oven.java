package mc.minicraft.component.entity;

import mc.minecraft.client.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.minecraft.client.screen.CraftingMenu;

public class Oven extends Furniture {
    public Oven() {
        super("Oven");
        col = Color.get(-1, 000, 332, 442);
        sprite = 2;
        xr = 3;
        yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.ovenRecipes, player));
        return true;
    }
}