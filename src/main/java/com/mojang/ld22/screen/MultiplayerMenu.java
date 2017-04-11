package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public final class MultiplayerMenu extends Menu {
    private final Menu parent;
    MultiplayerMenu(Menu parent) {
        this.parent = parent;
    }

    public void tick() {
        if (input.attack.clicked || input.menu.clicked) {
            game.setMenu(parent);
        }
    }

    public void render(Screen screen) {
        screen.clear(0);

        Font.draw("START", screen, 4 * 8 + 4, 1 * 8, Color.get(0, 555, 555, 555));
    }
}
