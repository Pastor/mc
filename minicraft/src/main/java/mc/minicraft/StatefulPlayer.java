package mc.minicraft;

import mc.api.Session;
import mc.minicraft.component.level.Level;

public interface StatefulPlayer extends Session.Listener {

    void tick();

    void resetPlayer(Level level);

    void registerPlayer(Level level);
}
