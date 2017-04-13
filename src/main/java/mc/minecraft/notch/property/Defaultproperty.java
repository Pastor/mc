package mc.minecraft.notch.property;

final class DefaultProperty extends AbstractProperty {
    DefaultProperty(Type type, String key, String description) {
        this(type, key, null, description);
    }

    DefaultProperty(Type type, String key, Object value, String description) {
        super(type, key, description);
        setValue(value);
    }
}
