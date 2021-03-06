package mc.minicraft.engine.level.tile;

import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.level.BaseLevel;

public class StoneTile extends Tile {
    public StoneTile(int id) {
        super(id);
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
        int rc1 = 111;
        int rc2 = 333;
        int rc3 = 555;
        screen.render(x * 16 + 0, y * 16 + 0, 32, Color.get(rc1, level.dirtColor, rc2, rc3), 0);
        screen.render(x * 16 + 8, y * 16 + 0, 32, Color.get(rc1, level.dirtColor, rc2, rc3), 0);
        screen.render(x * 16 + 0, y * 16 + 8, 32, Color.get(rc1, level.dirtColor, rc2, rc3), 0);
        screen.render(x * 16 + 8, y * 16 + 8, 32, Color.get(rc1, level.dirtColor, rc2, rc3), 0);
    }

    public boolean mayPass(BaseLevel level, int x, int y, Entity e) {
        return false;
    }

}
