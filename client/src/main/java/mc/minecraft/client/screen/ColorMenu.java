package mc.minecraft.client.screen;

import mc.minicraft.engine.gfx.Color;
import mc.minecraft.client.gfx.Font;
import mc.minecraft.client.gfx.ClientScreen;
import mc.minecraft.client.sound.Sound;

public final class ColorMenu extends Menu {
    private final Field[] fields = new Field[]{
            new Field("B", "500"),
            new Field("C", "500"),
            new Field("D", "500"),
            new Field("A", "-1")
    };
    private final Menu parent;
    private int nextId = 0;
    private int tickCount;

    ColorMenu(Menu parent) {
        super(parent.propertyReader);
        this.parent = parent;
    }

    @Override
    public void tick() {
        if (input.escape.clicked) {
            game.setMenu(parent);
        } else if (input.enter.clicked) {
            next();
        } else if (input.backspace.clicked && fields[nextId].value.length() > 0) {
            fields[nextId].value.setLength(fields[nextId].value.length() - 1);
        } else if (input.character.hasCharacter()) {
            if (fields[nextId].value.length() < 3 &&
                    input.character.ch <= '9' &&
                    input.character.ch >= '0' ||
                    input.character.ch == '-') {
                fields[nextId].value.append(input.character.ch);
            } else {
                Sound.test.play();
            }
        } else if (input.up.clicked) {
            previous();
        } else if (input.down.clicked) {
            next();
        }
        ++tickCount;
    }

    @Override
    public void render(ClientScreen screen) {
        screen.clear(0);
        int x = 8;
        int y = 8;
        int w = 6;
        int h = 1;

        int col = Color.get(5, 555, 555, 555);
        for (Field field : fields) {
            drawRect(screen, x, y, w, h);
            String text = String.format("%s:", field.name);
            Font.draw(text, screen, x, y, col);
            x += text.length() * 8;
            String value = field.value.toString();
            if (value.length() < 4) {
                for (int i = value.length(); i <= 3; ++i) {
                    value = value + " ";
                }
            }
            Font.draw(value, screen, x, y, col);
            y += 24;
            x = 8;
        }
        y = 8 + (nextId * 24);
        x = 24 + (fields[nextId].value.length() * 8);
        if ((tickCount / 20) % 2 == 0) {
            screen.render(x, y, 28 * 32, Color.get(5, 333, 333, 333), 0);
        } else {
            screen.render(x, y, 28 * 32, col, 0);
        }

        int color = color();
        for (int i = 8; i < game.width() - 8; i += 8) {
            screen.render(i, game.height() - 24, 12 * 32, color, 0);
        }
        Font.draw("ABCDEFGHIJKLMNOPQRSTUVWXYZ", screen, 8, game.height() - 16, color);
    }

    private static void drawRect(ClientScreen screen, int xx, int yy, int w, int h) {
        int row = 0;

        screen.render(xx - 8, yy - 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 0);
        screen.render(xx + w * 8, yy - 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 1);
        screen.render(xx - 8, yy + 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 2);
        screen.render(xx + w * 8, yy + 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 3);
        ++row;
        for (int x = 0; x < w; x++) {
            screen.render(xx + x * 8, yy - 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 0);
            screen.render(xx + x * 8, yy + 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 2);
        }
        ++row;
        for (int y = 0; y < h; y++) {
            screen.render(xx - 8, yy + y * 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 0);
            screen.render(xx + w * 8, yy + y * 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 1);
        }
    }

    private void next() {
        ++nextId;
        if (nextId >= fields.length)
            nextId = 0;
    }

    private void previous() {
        --nextId;
        if (nextId < 0)
            nextId = fields.length - 1;
    }

    private int color() {
        try {
            int a = Integer.parseInt(fields[3].value());
            int b = Integer.parseInt(fields[0].value());
            int c = Integer.parseInt(fields[1].value());
            int d = Integer.parseInt(fields[2].value());
            return Color.get(a, b, c, d);
        } catch (NumberFormatException ex) {
            return Color.get(0);
        }
    }

    private static final class Field {
        final String name;
        final StringBuilder value;

        private Field(String name, String value) {
            this.name = name;
            this.value = new StringBuilder(value);
        }

        String value() {
            if (value.toString().equals("-"))
                return "0";
            return value.length() == 0 ? "0" : value.toString();
        }
    }
}
