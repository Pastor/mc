package mc.minecraft.client.level.tile;

import mc.minicraft.engine.entity.AirWizard;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.gfx.Screen;
import mc.minecraft.client.level.Level;

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
