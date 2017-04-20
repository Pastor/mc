package mc.minicraft.engine.entity;

import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.gfx.Color;

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