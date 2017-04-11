package mc.game.data.status.handler;

import mc.api.Session;
import mc.game.data.status.ServerStatusInfo;

public interface ServerInfoBuilder {
    public ServerStatusInfo buildInfo(Session session);
}
