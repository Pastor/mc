package mc.minecraft.notch.property;

public interface PropertyReader {
    PropertyReadonly property(String key);

    void addListener(Listener listener);

    void removeListener(Listener listener);

    interface Listener {
        void update(PropertyReadonly value);
    }
}
