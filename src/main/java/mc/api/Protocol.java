package mc.api;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public abstract class Protocol {
    private final Map<Integer, Class<? extends Packet>> incoming = new HashMap<>();
    private final Map<Class<? extends Packet>, Integer> outgoing = new HashMap<>();

    public abstract String prefix();

    public abstract Packet.Header header();

    public abstract Packet.Encrypt encrypt();

    public abstract void newSession(Client client, Session session);

    public abstract void newSession(Server server, Session session);

    public final void register(int id, Class<? extends Packet> packet) {
        this.registerIncoming(id, packet);
        this.registerOutgoing(id, packet);
    }

    protected final void registerIncoming(int id, Class<? extends Packet> packet) {
        this.incoming.put(id, packet);
        try {
            this.createIncomingPacket(id);
        } catch(IllegalStateException e) {
            this.incoming.remove(id);
            throw new IllegalArgumentException(e.getMessage(), e.getCause());
        }
    }

    protected final void registerOutgoing(int id, Class<? extends Packet> packet) {
        this.outgoing.put(packet, id);
    }

    public final Packet createIncomingPacket(int id) {
        if(id < 0 || !this.incoming.containsKey(id) || this.incoming.get(id) == null) {
            throw new IllegalArgumentException("Invalid packet id: " + id);
        }

        Class<? extends Packet> packet = this.incoming.get(id);
        try {
            Constructor<? extends Packet> constructor = packet.getDeclaredConstructor();
            if(!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            return constructor.newInstance();
        } catch(NoSuchMethodError e) {
            throw new IllegalStateException("Packet \"" + id + ", " + packet.getName() + "\" does not have a no-params constructor for instantiation.");
        } catch(Exception e) {
            throw new IllegalStateException("Failed to instantiate packet \"" + id + ", " + packet.getName() + "\".", e);
        }
    }

    public final int getOutgoingId(Class<? extends Packet> packet) {
        if(!this.outgoing.containsKey(packet) || this.outgoing.get(packet) == null) {
            throw new IllegalArgumentException("Unregistered outgoing packet class: " + packet.getName());
        }

        return this.outgoing.get(packet);
    }

    protected void clear() {
        incoming.clear();
        outgoing.clear();
    }
}
