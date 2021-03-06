package mc.minecraft.notch;

import mc.minecraft.notch.console.CommandProcessor;
import mc.minecraft.notch.console.ConsoleManager;
import mc.minecraft.notch.screen.Menu;
import mc.minecraft.notch.entity.Player;
import mc.engine.property.PropertyReader;

public interface Game {

    String NAME = "Minicraft";

    int width();

    int height();

    int scale();

    PropertyReader propertyReader();

    ConsoleManager consoleManager();

    CommandProcessor commandProcessor();

    void stop();

    void setMenu(Menu menu);

    void resetGame();

    Player player();

    int gameTime();

    void changeLevel(int dir);

    void addKeyListener(InputHandler inputHandler);

    void scheduleLevelChange(int dir);

    void won();

    boolean connect(String username, String password, String hostname);

    boolean isConnected();
}
