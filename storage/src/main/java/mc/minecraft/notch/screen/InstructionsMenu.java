package mc.minecraft.client.screen;

import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.gfx.Font;
import mc.minicraft.engine.gfx.Screen;

final class InstructionsMenu extends Menu {
    private static final String MSG = "Move your character with the arrow keys press C to attack and X to " +
            "open the inventory and to use items. Select an item in the inventory to equip it. Kill the air wizard" +
            " to win the game!";
    private final Menu parent;


    InstructionsMenu(Menu parent) {
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

        String how = "HOW TO PLAY";
        int x = 4;
        int y = 8;
        Font.draw(how, screen, x + ((game.width() - 16) / 2 - (how.length() * 8) / 2), y, Color.get(0, 555, 555, 555));
        int offset = 48;
        y = 3 * 8;
        Font.draw(MSG, screen, x + offset, y, game.width() - offset, game.height() - 32, Color.get(0, 333, 333, 333));
    }
}
