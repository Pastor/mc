package mc.minecraft.notch.property;

public interface PropertyContainer extends PropertyReader, PropertyWriter {
    void register(AbstractProperty property);
}
