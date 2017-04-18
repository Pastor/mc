package mc.minicraft.component.entity;

import mc.engine.property.PropertyReader;
import mc.minicraft.component.gfx.Color;
import mc.api.Sound;
import mc.minicraft.data.game.entity.EntityType;

public class Chest extends Furniture {
    public final Inventory inventory = new Inventory();

    public Chest(Sound sound, PlayerHandler handler, PropertyReader reader) {
        super(sound, handler, reader, "Chest", EntityType.CHEST);
        col = Color.get(-1, 110, 331, 552);
        sprite = 1;
    }

    public boolean use(Player player, int attackDir) {
        handler.containerMenu(player, "Chest", inventory);
        return true;
    }
}