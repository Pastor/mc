package mc.minecraft.client.screen;

import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.gfx.Font;
import mc.minicraft.engine.gfx.Screen;

import java.awt.*;

final class AboutMenu extends Menu {
    private static final String MSG = "Minicraft was made by Markus Persson For the 22'nd ludum " +
            "dare competition in december 2011. it is dedicated to my father. <3";
    private static final String APPENDIX = "This game was modify by Pastor in april 2017.";
    private final Menu parent;

    AboutMenu(Menu parent) {
        super(parent.propertyReader);
        this.parent = parent;
    }

    public void tick() {
        if (input.attack.clicked || input.menu.clicked || input.escape.clicked) {
            game.setMenu(parent);
        }
    }

    public void render(Screen screen) {
        screen.clear(0);
        String about = "About Minicraft";

        int x = 4;
        int y = 8;
        Font.draw(about, screen, x + ((game.width() - 16) / 2 - (about.length() * 8) / 2), y, Color.get(0, 555, 555, 555));

        int offset = 48;
        y = 3 * 8;
        Point next = Font.draw(MSG, screen, x + offset, y, game.width() - offset, game.height() - 32, Color.get(0, 333, 333, 333));
        y = next.y + 3 * 8;
        Font.draw(APPENDIX, screen, x + offset, y, game.width() - offset, game.height() - 32, Color.get(0, 333, 333, 333));
    }
}
