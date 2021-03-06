package mc.minecraft.client.screen;

import mc.engine.property.PropertyReader;
import mc.minecraft.client.gfx.ClientScreen;

public final class LevelTransitionMenu extends Menu {
    private int dir;
    private int time = 0;

    public LevelTransitionMenu(int dir, PropertyReader propertyReader) {
        super(propertyReader);
        this.dir = dir;
    }

    public void tick() {
        time += 2;
        if (time == 30)
            game.changeLevel(dir);
        if (time == 60)
            game.setMenu(null);
    }

    public void render(ClientScreen screen) {
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 15; y++) {
                int dd = (y + x % 2 * 2 + x / 3) - time;
                if (dd < 0 && dd > -30) {
                    if (dir > 0)
                        screen.render(x * 8, y * 8, 0, 0, 0);
                    else
                        screen.render(x * 8, screen.h - y * 8 - 8, 0, 0, 0);
                }
            }
        }
    }
}
