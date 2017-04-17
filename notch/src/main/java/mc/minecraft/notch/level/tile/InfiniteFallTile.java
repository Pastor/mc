package mc.minecraft.notch.level.tile;

import mc.minecraft.notch.entity.AirWizard;
import mc.minecraft.notch.entity.Entity;
import mc.minecraft.notch.gfx.Screen;
import mc.minecraft.notch.level.Level;

public class InfiniteFallTile extends Tile {
    public InfiniteFallTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
    }

    public void tick(Level level, int xt, int yt) {
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        if (e instanceof AirWizard) return true;
        return false;
    }
}
