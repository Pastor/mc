package mc.minicraft.data.status.handler;

import mc.api.Session;

public interface ServerPingTimeHandler {
    public void handle(Session session, long pingTime);
}
