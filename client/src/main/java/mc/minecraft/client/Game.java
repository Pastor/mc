package mc.minecraft.client;

import mc.minecraft.client.console.CommandProcessor;
import mc.minecraft.client.console.ConsoleManager;
import mc.minicraft.engine.entity.Player;
import mc.engine.property.PropertyReader;
import mc.minecraft.client.screen.Menu;

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

    void respawn();

    Player player();

    int gameTime();

    void changeLevel(int dir);

    void addKeyListener(InputHandler inputHandler);

    void won();

    boolean connect(String username, String password, String hostname);

    boolean isConnected();

    void mainMenu();
}
