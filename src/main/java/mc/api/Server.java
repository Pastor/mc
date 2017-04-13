package mc.api;

import java.util.Map;

public interface Server {

    ConnectionListener newConnectionListener();

    Map<? extends String, ?> getGlobalFlags();

    void addSession(Session session);

    void removeSession(Session session);

    Protocol createProtocol();

    void setGlobalFlag(String key, Object property);

    void addListener(Listener listener);

    void close();

    Server bind();

    Server bind(boolean wait);

    void sendBroadcast(Packet packet, Session exclude);

    abstract class Event extends mc.api.Event<Server.Listener> {
        public final Server server;
        public final Session session;

        public Event(Server server, Session session) {
            this.server = server;
            this.session = session;
        }
    }

    interface Listener extends Event.Listener {

        void serverBound(Event event);

        void serverClosing(Event event);

        void serverClosed(Event event);

        void sessionAdded(Event event);

        void sessionRemoved(Event event);
    }

    abstract class ListenerAdapter implements Listener {
        @Override
        public void serverBound(Event event) {

        }

        @Override
        public void serverClosing(Event event) {

        }

        @Override
        public void serverClosed(Event event) {

        }

        @Override
        public void sessionAdded(Event event) {

        }

        @Override
        public void sessionRemoved(Event event) {

        }
    }

    interface ConnectionListener {

        String getHost();

        int getPort();

        boolean isListening();

        void bind();

        void bind(boolean wait);

        void bind(boolean wait, Runnable callback);

        void close();

        void close(boolean wait);

        void close(boolean wait, Runnable callback);
    }
}
