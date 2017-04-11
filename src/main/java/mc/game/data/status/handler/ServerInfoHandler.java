package mc.game.data.status.handler;


import mc.api.Session;
import mc.game.data.status.ServerStatusInfo;

public interface ServerInfoHandler {
    public void handle(Session session, ServerStatusInfo info);
}
