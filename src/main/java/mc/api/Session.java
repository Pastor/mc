package mc.api;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public interface Session {

    void connect();

    void connect(boolean wait);

    void connect(String hostname, boolean wait);

    String host();

    int port();

    SocketAddress localAddress();

    SocketAddress remoteAddress();

    Protocol protocol();

    Map<String, Object> flags();

    boolean hasFlag(String key);

    <T> T flag(String key);

    void setFlag(String key, Object value);

    List<Event.Listener> listeners();

    void addListener(Session.Listener listener);

    void removeListener(Session.Listener listener);

    void callEvent(Session.Event event);

    int getCompressionThreshold();

    void setCompressionThreshold(int threshold);

    int connectTimeout();

    void setConnectTimeout(int timeout);

    int readTimeout();

    void setReadTimeout(int timeout);

    int writeTimeout();

    void setWriteTimeout(int timeout);

    boolean isConnected();

    void send(Packet packet);

    void disconnect(String reason);

    void disconnect(String reason, boolean wait);

    void disconnect(String reason, Throwable cause);

    void disconnect(String reason, Throwable cause, boolean wait);

    abstract class Event extends mc.api.Event<Session.Listener> {
        public final Session session;

        public Event(Session session) {
            this.session = session;
        }
    }

    abstract class DisconnectEvent extends Event {
        public final String reason;
        public final Throwable cause;

        protected DisconnectEvent(Session session, String reason, Throwable cause) {
            super(session);
            this.reason = reason;
            this.cause = cause;
        }

        public final Packet packet() {
            return null;
        }
    }

    interface Listener extends mc.api.Event.Listener {

        void packetReceived(Event event);

        void packetSent(Event event);

        void connected(Event event);

        void disconnecting(DisconnectEvent event);

        void disconnected(DisconnectEvent event);
    }

    abstract class ListenerAdapter implements Listener {
        @Override
        public void packetReceived(Event event) {

        }

        @Override
        public void packetSent(Event event) {

        }

        @Override
        public void connected(Event event) {

        }

        @Override
        public void disconnecting(DisconnectEvent event) {

        }

        @Override
        public void disconnected(DisconnectEvent event) {

        }
    }

    interface Factory {
        Session newSession(Client client);
    }
}
