package mc.minicraft.engine.level.tile;

import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Mob;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.level.BaseLevel;

public class SaplingTile extends Tile {
    private Tile onType;
    private Tile growsTo;

    public SaplingTile(int id, Tile onType, Tile growsTo) {
        super(id);
        this.onType = onType;
        this.growsTo = growsTo;
        connectsToSand = onType.connectsToSand;
        connectsToGrass = onType.connectsToGrass;
        connectsToWater = onType.connectsToWater;
        connectsToLava = onType.connectsToLava;
    }

    public void render(Screen screen, BaseLevel level, int x, int y) {
        onType.render(screen, level, x, y);
        int col = Color.get(10, 40, 50, -1);
        screen.render(x * 16 + 4, y * 16 + 4, 11 + 3 * 32, col, 0);
    }

    public void tick(BaseLevel level, int x, int y) {
        int age = level.getData(x, y) + 1;
        if (age > 100) {
            level.setTile(x, y, growsTo, 0);
        } else {
            level.setData(x, y, age);
        }
    }

    public void hurt(BaseLevel level, int x, int y, Mob source, int dmg, int attackDir) {
        level.setTile(x, y, onType, 0);
    }
}