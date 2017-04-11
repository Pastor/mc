package mc.minecraft.data.status.handler;


import mc.api.Session;
import mc.minecraft.data.status.ServerStatusInfo;

public interface ServerInfoHandler {
    public void handle(Session session, ServerStatusInfo info);
}
