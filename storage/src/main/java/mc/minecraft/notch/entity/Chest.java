package mc.minicraft.component.entity;

import mc.minicraft.component.gfx.Color;
import mc.minecraft.client.screen.ContainerMenu;

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