package mc.minecraft;

import mc.api.Session;
import mc.minecraft.notch.level.Level;

public interface StatefulPlayer extends Session.Listener {

    void tick();

    void resetPlayer(Level level);

    void registerPlayer(Level level);
}
