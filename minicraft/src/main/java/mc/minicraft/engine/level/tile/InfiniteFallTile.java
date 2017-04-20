package mc.minicraft.engine.level.tile;

import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.AirWizard;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.level.BaseLevel;

public class InfiniteFallTile extends Tile {
    public InfiniteFallTile(int id) {
        super(id);
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
    }

    public void tick(BaseLevel level, int xt, int yt) {
    }

    public boolean mayPass(BaseLevel level, int x, int y, Entity e) {
        if (e instanceof AirWizard) return true;
        return false;
    }
}
