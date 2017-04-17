package mc.minicraft.data.status.handler;

import mc.api.Session;
import mc.minicraft.data.status.ServerStatusInfo;

public interface ServerInfoBuilder {
    ServerStatusInfo buildInfo(Session session);
}
