package mc.minicraft.data.status.handler;


import mc.api.Session;
import mc.minicraft.data.status.ServerStatusInfo;

public interface ServerInfoHandler {
    public void handle(Session session, ServerStatusInfo info);
}
