package mc.minecraft.notch.screen;

import mc.minecraft.notch.gfx.Color;
import mc.minecraft.notch.gfx.Font;
import mc.minecraft.notch.gfx.Screen;
import mc.minecraft.notch.sound.Sound;

final class ConnectMenu extends Menu {
    private final Widget[] lines = new Widget[]{
            new InputText("Имя   :", 30),
            new InputPassword("Пароль:", 30),
            new InputText("Сервер:", 30, "127.0.0.1"),
            new Button("Вход", 4, new Runnable() {
                @Override
                public void run() {
                    ConnectMenu.this.game.connect(
                            ((InputText) lines[0]).value.toString(),
                            ((InputText) lines[1]).value.toString(),
                            ((InputText) lines[2]).value.toString());
                    ConnectMenu.this.parent.refresh();
                    ConnectMenu.this.game.setMenu(ConnectMenu.this.parent);
                }
            })
    };
    private final Menu parent;
    private int nextId = 0;
    private int tickCount;

    ConnectMenu(Menu parent) {
        super(parent.propertyReader);
        this.parent = parent;
    }

    @Override
    public void tick() {
        if (input.escape.clicked) {
            game.setMenu(parent);
        } else if (input.enter.clicked) {
            if (lines[nextId] instanceof Button) {
                lines[nextId].run();
            } else {
                next();
            }
        } else if (input.backspace.clicked && lines[nextId].isText()) {
            InputText it = (InputText) lines[nextId];
            if (it.value.length() > 0) {
                it.value.setLength(it.value.length() - 1);
            }
        } else if (input.character.hasCharacter() && lines[nextId].isText()) {
            InputText it = (InputText) lines[nextId];
            if (it.value.length() < it.inputLength()) {
                it.value.append(input.character.ch);
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
    public void render(Screen screen) {
        screen.clear(0);
        int x = 8;
        int y = 8;
        int h = 1;


        int mWidth = 8;
        for (Widget w : lines) {
            mWidth = Math.max(mWidth, w.width);
        }
        int xStart = (game.width() / 2) - ((mWidth / 2) * 8);
        int yStart = (game.height() / 2) - ((lines.length / 2) * 8);

        x = xStart;
        y = yStart;

        int defaultColor = Color.get(5, 555, 555, 555);
        int selectedColor = Color.get(5, 555, 555, 550);
        for (int i = 0; i < lines.length; i++) {
            Widget widget = lines[i];
            drawRect(screen, x, y, widget.width, h);
            int color = (i == nextId) ? selectedColor : defaultColor;
            Font.draw(fill(' ', widget.width), screen, x, y, color);
            if (widget.isText()) {
                InputText it = (InputText) widget;
                Font.draw(it.label, screen, x, y, color);
                x += it.label.length() * 8;
                String value = it.visible();
                Font.draw(value, screen, x, y, color);
            } else {
                Font.draw(widget.visible(), screen, x, y, color);
            }
            y += 24;
            x = xStart;
        }
        Widget widget = lines[nextId];
        if (widget.isText()) {
            InputText it = (InputText) widget;
            y = yStart + (nextId * 24);
            String visible = it.visible();
            x = xStart + (it.label.length() * 8) + (visible.length() * 8);
            if ((tickCount / 20) % 2 == 0) {
                screen.render(x, y, 28 * 32, Color.get(5, 333, 333, 333), 0);
            } else {
                screen.render(x, y, 28 * 32, defaultColor, 0);
            }
        }
    }

    private static void drawRect(Screen screen, int xx, int yy, int w, int h) {
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
        if (nextId >= lines.length)
            nextId = 0;
    }

    private void previous() {
        --nextId;
        if (nextId < 0)
            nextId = lines.length - 1;
    }

    static abstract class Widget implements Runnable {
        final int width;
        private final Runnable target;

        Widget(int width, Runnable action) {
            this.width = width;
            this.target = action;
        }

        Widget(int width) {
            this(width, null);
        }

        @Override
        public void run() {
            if (target != null)
                target.run();
        }

        abstract String visible();

        boolean isText() {
            return true;
        }

    }

    static class InputText extends Widget {
        final String label;
        final StringBuilder value = new StringBuilder();

        InputText(String label, int width, Runnable action) {
            super(width, action);
            this.label = label;
        }

        InputText(String label, int width) {
            super(width);
            this.label = label;
        }

        InputText(String label, int width, String value) {
            super(width);
            this.label = label;
            this.value.append(value);
        }

        void reset() {
            value.setLength(0);
        }

        int inputLength() {
            return width - label.length();
        }

        @Override
        String visible() {
            return value.toString();
        }
    }

    private static class InputPassword extends InputText {

        InputPassword(String label, int width) {
            super(label, width);
        }

        @Override
        String visible() {
            StringBuilder result = new StringBuilder();
            for (char unused : value.toString().toCharArray()) {
                result.append("*");
            }
            return result.toString();
        }
    }

    private static class Button extends Widget {
        final String label;

        Button(String label, int width, Runnable action) {
            super(width, action);
            this.label = label;
        }


        @Override
        String visible() {
            return label;
        }

        @Override
        boolean isText() {
            return false;
        }
    }

    private static String fill(char ch, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            builder.append(ch);
        }
        return builder.toString();
    }
}
