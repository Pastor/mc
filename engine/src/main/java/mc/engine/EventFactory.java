package mc.engine;

import mc.api.Packet;
import mc.api.Server;
import mc.api.Session;

public abstract class EventFactory {
    public Session.Event newDisconnectingEvent(Session session, String reason, Throwable cause) {
        return new Session.DisconnectEvent(session, reason, cause) {
            public void call(Session.Listener listener) {
                listener.disconnecting(this);
            }
        };
    }

    public Session.Event newDisconnectedEvent(Session session, String reason, Throwable cause) {
        return new Session.DisconnectEvent(session, reason, cause) {
            public void call(Session.Listener listener) {
                listener.disconnecting(this);
            }
        };
    }

    public Session.Event newConnectedEvent(Session session) {
        return new CommonSessionEvent(null, session) {
            public void call(Session.Listener listener) {
                listener.connected(this);
            }
        };
    }

    public Session.Event newPacketReceivedEvent(Session session, Packet packet) {

        return new CommonSessionEvent(packet, session) {
            public void call(Session.Listener listener) {
                listener.packetReceived(this);
            }
        };
    }

    public Session.Event newPacketSentEvent(Session session, Packet packet) {

        return new CommonSessionEvent(packet, session) {
            public void call(Session.Listener listener) {
                listener.packetSent(this);
            }
        };
    }

    public Server.Event newServerBoundEvent(Server server) {

        return new CommonServerEvent(null, server, null) {
            public void call(Server.Listener listener) {
                listener.serverBound(this);
            }
        };
    }

    public Server.Event newSessionAddedEvent(Server server, Session session) {
        return new CommonServerEvent(null, server, session) {
            public void call(Server.Listener listener) {
                listener.sessionAdded(this);
            }
        };
    }

    public Server.Event newSessionRemovedEvent(Server server, Session session) {
        return new CommonServerEvent(null, server, session) {
            public void call(Server.Listener listener) {
                listener.sessionRemoved(this);
            }
        };
    }

    public Server.Event newServerClosingEvent(Server server) {
        return new CommonServerEvent(null, server, null) {
            public void call(Server.Listener listener) {
                listener.serverClosing(this);
            }
        };
    }

    public Server.Event newServerClosedEvent(Server server) {
        return new CommonServerEvent(null, server, null) {
            public void call(Server.Listener listener) {
                listener.serverClosed(this);
            }
        };
    }

    private static abstract class CommonSessionEvent extends Session.Event {
        private final Packet packet;

        private CommonSessionEvent(Packet packet, Session session) {
            super(session);
            this.packet = packet;
        }

        @Override
        public final Packet packet() {
            return packet;
        }

        public abstract void call(Session.Listener listener);
    }

    private static abstract class CommonServerEvent extends Server.Event {
        private final Packet packet;

        private CommonServerEvent(Packet packet, Server server, Session session) {
            super(server, session);
            this.packet = packet;
        }

        @Override
        public Packet packet() {
            return packet;
        }

        @Override
        public abstract void call(Server.Listener listener);
    }

    private static final class Instance {
        private static final EventFactory instance = new EventFactory() {
        };
    }

    public static EventFactory instance() {
        return Instance.instance;
    }
}
