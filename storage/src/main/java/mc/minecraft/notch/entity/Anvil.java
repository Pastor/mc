package mc.minicraft.component.entity;

import mc.minecraft.client.crafting.Crafting;
import mc.minicraft.component.gfx.Color;
import mc.minecraft.client.screen.CraftingMenu;

public class Anvil extends Furniture {
    public Anvil() {
        super("Anvil");
        col = Color.get(-1, 000, 111, 222);
        sprite = 0;
        xr = 3;
        yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.anvilRecipes, player));
        return true;
    }
}