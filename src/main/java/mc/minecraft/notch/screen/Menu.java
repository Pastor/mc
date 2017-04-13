package mc.minecraft.notch.screen;

import mc.minecraft.notch.Game;
import mc.minecraft.notch.InputHandler;
import mc.minecraft.notch.gfx.Color;
import mc.minecraft.notch.gfx.Font;
import mc.minecraft.notch.gfx.Screen;
import mc.minecraft.notch.property.PropertyReader;

import java.util.List;

public class Menu {
    public final PropertyReader propertyReader;
    protected Game game;
    protected InputHandler input;

    protected Menu(PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    public void init(Game game, InputHandler input) {
        this.input = input;
        this.game = game;
    }

    public void tick() {
    }

    public void render(Screen screen) {
    }

    public void renderItemList(Screen screen, int xo, int yo, int x1, int y1, List<? extends ListItem> listItems, int selected) {
        boolean renderCursor = true;
        if (selected < 0) {
            selected = -selected - 1;
            renderCursor = false;
        }
        int w = x1 - xo;
        int h = y1 - yo - 1;
        int i0 = 0;
        int i1 = listItems.size();
        if (i1 > h) i1 = h;
        int io = selected - h / 2;
        if (io > listItems.size() - h) io = listItems.size() - h;
        if (io < 0) io = 0;

        for (int i = i0; i < i1; i++) {
            listItems.get(i + io).renderInventory(screen, (1 + xo) * 8, (i + 1 + yo) * 8);
        }

        if (renderCursor) {
            int yy = selected + 1 - io + yo;
            Font.draw(">", screen, (xo + 0) * 8, yy * 8, Color.get(5, 555, 555, 555));
            Font.draw("<", screen, (xo + w) * 8, yy * 8, Color.get(5, 555, 555, 555));
        }
    }
}
