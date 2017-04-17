package mc.minecraft.notch.entity;

import mc.minecraft.notch.gfx.Color;
import mc.minecraft.notch.screen.ContainerMenu;

public class Chest extends Furniture {
    public Inventory inventory = new Inventory();

    public Chest() {
        super("Chest");
        col = Color.get(-1, 110, 331, 552);
        sprite = 1;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new ContainerMenu(player, "Chest", inventory));
        return true;
    }
}