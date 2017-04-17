package mc.engine.property;

public interface PropertyContainer extends PropertyReader, PropertyWriter {
    void register(AbstractProperty property);
}
