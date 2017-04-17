package mc.minicraft.component.entity;

import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.sound.Sound;

public class Lantern extends Furniture {
    public Lantern(Sound sound) {
        super(sound, "Lantern");
        col = Color.get(-1, 000, 111, 555);
        sprite = 5;
        xr = 3;
        yr = 2;
    }

    public int getLightRadius() {
        return 8;
    }
}