package mc.engine.tcp;

import mc.api.Client;
import mc.api.PacketConstructor;
import mc.api.Protocol;
import mc.api.Session;

public final class DefaultClient implements Client {
    private final String host;
    private final int port;
    private final Protocol protocol;
    private final Session.Factory factory;
    private final PacketConstructor constructor;
    private Session session;

    public DefaultClient(String host, int port, Protocol protocol, Session.Factory factory, PacketConstructor constructor) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.factory = factory;
        this.constructor = constructor;
        this.session = factory.newSession(this, constructor);
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public Protocol protocol() {
        return protocol;
    }

    @Override
    public Session session() {
        if (session == null || session.isDisconnected())
            newSession();
        return session;
    }

    @Override
    public void newSession() {
        session = factory.newSession(this, constructor);
    }

}
