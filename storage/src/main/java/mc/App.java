package mc;

import mc.minicraft.game.MinicraftGameServer;
import mc.minecraft.client.MinicraftGame;

import java.net.Proxy;

//-Dmaven.test.skip=true
public final class App {
    private static final boolean SPAWN_SERVER = true;
    private static final Proxy PROXY = Proxy.NO_PROXY;

    public static void main(String[] args) {
        if (SPAWN_SERVER) {
            MinicraftGameServer.createServer().start();
        }
        startGame();
        startGame();
    }

    private static void startGame() {
        MinicraftGame.startGame(PROXY, true);
    }
}
