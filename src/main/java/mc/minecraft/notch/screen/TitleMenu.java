package mc.minecraft.notch.screen;

import mc.minecraft.notch.console.ConsoleMenu;
import mc.minecraft.notch.gfx.Color;
import mc.minecraft.notch.gfx.Font;
import mc.minecraft.notch.gfx.Screen;
import mc.minecraft.notch.property.PropertyConstants;
import mc.minecraft.notch.property.PropertyReader;
import mc.minecraft.notch.sound.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class TitleMenu extends Menu {
    public final ConsoleMenu consoleMenu;
    private int selected = 0;
    private static final List<Option<TitleMenu>> defaultOptions = new ArrayList<Option<TitleMenu>>() {
        {
            add(new Option<>("Start game", "Запуск игры", titleMenu -> {
                Sound.test.play();
                titleMenu.game.resetGame();
                titleMenu.game.setMenu(null);
                return null;
            }));
            add(new Option<>("Login", "Авторизация", titleMenu -> {
                titleMenu.game.setMenu(new LoginMenu(titleMenu));
                return null;
            }));
            add(new Option<>("How to play", "Описание", titleMenu -> {
                titleMenu.game.setMenu(new InstructionsMenu(titleMenu));
                return null;
            }));
            add(new Option<>("About", "О игре", titleMenu -> {
                titleMenu.game.setMenu(new AboutMenu(titleMenu));
                return null;
            }));
            add(new Option<>("Quit", "Выход из игры", titleMenu -> {
                titleMenu.game.stop();
                return null;
            }));
        }
    };
    private static final List<Option<TitleMenu>> developmentOptions = new ArrayList<Option<TitleMenu>>() {
        {
            add(new Option<>("Console", "Консоль разработчика", titleMenu -> {
                titleMenu.game.setMenu(titleMenu.consoleMenu);
                return null;
            }));
            add(new Option<>("Color", "Визуальное отображение цвета", titleMenu -> {
                titleMenu.game.setMenu(new ColorMenu(titleMenu));
                return null;
            }));
        }
    };

    private final List<Option<TitleMenu>> options = new ArrayList<>();

    public TitleMenu(PropertyReader propertyReader) {
        super(propertyReader);
        consoleMenu = new ConsoleMenu(this);
        buildOptions();
        propertyReader.addListener(value -> {
            if ("development".equals(value.key())) {
                buildOptions();
            }
        });
    }

    public void tick() {
        if (input.up.clicked) selected--;
        if (input.down.clicked) selected++;

        int len = options.size();
        if (selected < 0) selected += len;
        if (selected >= len) selected -= len;

        if (selected >= len)
            selected = len - 1;

        if (input.attack.clicked || input.menu.clicked) {
            option(selected).action.apply(this);
        }
    }

    private void buildOptions() {
        options.clear();
        options.addAll(defaultOptions);
        if (propertyReader.property(PropertyConstants.DEVELOPMENT).asValue()) {
            options.addAll(developmentOptions);
        }
    }

    public void render(Screen screen) {
        screen.clear(0);

        int h = 2;
        int w = 13;
        int titleColor = Color.get(0, 010, 131, 551);
        int xo = (screen.w - w * 8) / 2;
        int yo = 24;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                screen.render(xo + x * 8, yo + y * 8, x + (y + 6) * 32, titleColor, 0);
            }
        }

        for (int i = 0; i < options.size(); i++) {
            String msg = option(i).optionName;
            int col = Color.get(0, 222, 222, 222);
            if (i == selected) {
                msg = "> " + msg + " <";
                col = Color.get(0, 555, 555, 555);
            }
            Font.draw(msg, screen, (screen.w - msg.length() * 8) / 2, (8 + i) * 8, col);
        }

        Font.draw(option(selected).description, screen, 0, screen.h - 16, Color.get(0, 111, 111, 111));
        Font.draw("(Arrow keys,X and C)", screen, 0, screen.h - 8, Color.get(0, 111, 111, 111));
    }

    private Option<TitleMenu> option(int i) {
        if (i < 0 || i >= options.size())
            return options.get(0);
        return options.get(i);
    }

    private static final class Option<E> {
        final String optionName;
        final String description;
        final Function<E, Void> action;

        private Option(String optionName, String description, Function<E, Void> action) {
            this.optionName = optionName;
            this.description = description;
            this.action = action;
        }
    }
}