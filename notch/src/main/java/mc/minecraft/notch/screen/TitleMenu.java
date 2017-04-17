package mc.minecraft.notch.screen;

import mc.engine.property.PropertyReader;
import mc.minecraft.notch.console.ConsoleMenu;
import mc.minecraft.notch.gfx.Color;
import mc.minecraft.notch.gfx.Font;
import mc.minecraft.notch.gfx.Screen;
import mc.minecraft.notch.sound.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class TitleMenu extends Menu {

    private static final int OPTION_START_GAME = 0;
    private static final int OPTION_LOGIN = 1;
    private static final int OPTION_CONSOLE = 2;
    private static final int OPTION_COLOR = 3;
    private static final int OPTION_HOW_PLAY = 4;
    private static final int OPTION_ABOUT = 5;
    private static final int OPTION_QUIT = 6;

    public final ConsoleMenu consoleMenu;
    private int selected = 0;
    private static final List<Option<TitleMenu>> defaultOptions = new ArrayList<Option<TitleMenu>>(20) {
        {
            add(OPTION_START_GAME, new Option<>("Start game", "Запуск игры", titleMenu -> {
                Sound.test.play();
                titleMenu.game.resetGame();
                titleMenu.game.setMenu(null);
                return null;
            }, true));
            add(OPTION_LOGIN, new Option<>("Connect", "Авторизация", titleMenu -> {
                titleMenu.game.setMenu(new ConnectMenu(titleMenu));
                return null;
            }));

            add(OPTION_CONSOLE, new Option<>("Console", "Консоль разработчика", titleMenu -> {
                titleMenu.game.setMenu(titleMenu.consoleMenu);
                return null;
            }, false));
            add(OPTION_COLOR, new Option<>("Color", "Визуальное отображение цвета", titleMenu -> {
                titleMenu.game.setMenu(new ColorMenu(titleMenu));
                return null;
            }, false));

            add(OPTION_HOW_PLAY, new Option<>("How to play", "Описание", titleMenu -> {
                titleMenu.game.setMenu(new InstructionsMenu(titleMenu));
                return null;
            }));
            add(OPTION_ABOUT, new Option<>("About", "О игре", titleMenu -> {
                titleMenu.game.setMenu(new AboutMenu(titleMenu));
                return null;
            }));
            add(OPTION_QUIT, new Option<>("Quit", "Выход из игры", titleMenu -> {
                titleMenu.game.stop();
                return null;
            }));
        }
    };

    private final List<Option<TitleMenu>> options = new ArrayList<>();

    public TitleMenu(PropertyReader propertyReader) {
        super(propertyReader);
        consoleMenu = new ConsoleMenu(this);
        propertyReader.addListener(value -> {
            if ("development".equals(value.key())) {
                defaultOptions.get(OPTION_COLOR).isVisible = value.asValue();
                defaultOptions.get(OPTION_CONSOLE).isVisible = value.asValue();
                refresh();
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

    public void refresh() {
        options.clear();

        if (game != null) {
            defaultOptions.get(OPTION_LOGIN).isVisible = !game.isConnected();
//            defaultOptions.get(OPTION_START_GAME).isVisible = game.isConnected();
        }

        for (Option<TitleMenu> option : defaultOptions) {
            if (option.isVisible) {
                options.add(option);
            }
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
        boolean isVisible;

        private Option(String optionName, String description, Function<E, Void> action) {
            this(optionName, description, action, true);
        }

        private Option(String optionName, String description, Function<E, Void> action, boolean isVisible) {
            this.optionName = optionName;
            this.description = description;
            this.action = action;
            this.isVisible = isVisible;
        }
    }
}