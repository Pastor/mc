package mc.minicraft.component.entity;

import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.sound.Sound;

public class Chest extends Furniture {
    private final PlayerHandler handler;
    public final Inventory inventory = new Inventory();

    public Chest(Sound sound, PlayerHandler handler) {
        super(sound, "Chest");
        this.handler = handler;
        col = Color.get(-1, 110, 331, 552);
        sprite = 1;
    }

    public boolean use(Player player, int attackDir) {
        handler.containerMenu(player, "Chest", inventory);
        return true;
    }
}