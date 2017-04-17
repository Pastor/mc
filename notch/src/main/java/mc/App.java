package mc;

import mc.minecraft.notch.MinicraftGame;

import java.net.Proxy;

//-Dmaven.test.skip=true
public final class App {

    public static void main(String[] args) {
        startGame();
    }

    private static void startGame() {
        MinicraftGame.startGame(Proxy.NO_PROXY, true);
    }
}
