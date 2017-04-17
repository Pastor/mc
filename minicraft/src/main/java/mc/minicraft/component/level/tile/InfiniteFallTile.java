package mc.minicraft.component.level.tile;

import mc.minicraft.component.Screen;
import mc.minicraft.component.entity.AirWizard;
import mc.minicraft.component.entity.Entity;
import mc.minicraft.component.level.Level;

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
