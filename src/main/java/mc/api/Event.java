package mc.api;

public abstract class Event<ListenerType> {

    public abstract Packet packet();

    @SuppressWarnings("unchecked")
    public final <E extends Packet> E asPacket() {
        Packet packet = packet();
        return (E) packet;
    }

    public abstract void call(ListenerType listener);

    public interface Listener {

    }
}
