package mc.engine.property;

public interface PropertyReader {
    Property property(String key);

    void addListener(Listener listener);

    void removeListener(Listener listener);

    interface Listener {
        void update(Property value);
    }
}
