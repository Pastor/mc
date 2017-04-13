package mc.engine.tcp;

import mc.api.Client;
import mc.api.Protocol;
import mc.api.Provider;
import mc.api.Session;

public final class DefaultClient implements Client {
    private final String host;
    private final int port;
    private final Provider<Protocol> protocol;
    private final Session session;

    public DefaultClient(String host, int port, Provider<Protocol> protocol, Session.Factory factory) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.session = factory.newSession(this);
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
    public Provider<Protocol> protocol() {
        return protocol;
    }

    @Override
    public Session session() {
        return session;
    }
}
