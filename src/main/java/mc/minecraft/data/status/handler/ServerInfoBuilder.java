package mc.minecraft.data.status.handler;

import mc.api.Session;
import mc.minecraft.data.status.ServerStatusInfo;

public interface ServerInfoBuilder {
    public ServerStatusInfo buildInfo(Session session);
}
