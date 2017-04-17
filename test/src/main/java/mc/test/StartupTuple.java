package mc.test;

public final class StartupTuple {
    public static void main(String[] args) {
        mc.minicraft.server.App.main(args);
        mc.minecraft.client.App.main(args);
    }
}
