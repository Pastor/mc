package mc;

import mc.minecraft.client.MinicraftGame;

import java.net.Proxy;

//-Dmaven.test.skip=true
public final class App {
    private static final Proxy PROXY = Proxy.NO_PROXY;

    public static void main(String[] args) {
        startGame();
//        startGame();
    }

    private static void startGame() {
        MinicraftGame.startGame(PROXY, true);
    }
}
