package mc.minicraft.component.entity;

import mc.engine.property.PropertyReader;
import mc.minicraft.component.gfx.Color;
import mc.api.Sound;
import mc.minicraft.data.game.entity.EntityType;

public final class Lantern extends Furniture {
    public Lantern(Sound sound, PlayerHandler handler, PropertyReader reader) {
        super(sound, handler, reader, "Lantern", EntityType.LANTERN);
        col = Color.get(-1, 000, 111, 555);
        sprite = 5;
        xr = 3;
        yr = 2;
    }

    public int getLightRadius() {
        return 8;
    }
}