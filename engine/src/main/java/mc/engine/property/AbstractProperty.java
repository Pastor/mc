package mc.engine.property;

abstract class AbstractProperty implements Property {
    private final Type type;
    private final String key;
    private final String description;
    private Object value;

    AbstractProperty(Type type, String key, String description) {
        this.type = type;
        this.key = key;
        this.description = description;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Object value() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E asValue() {
        if (value != null) {
            return (E) value;
        }
        return null;
    }

    @Override
    public String asString() {
        if (value == null)
            return "";
        return value.toString();
    }

    void setValue(Object value) {
        this.value = type().convert(value);
    }

    @Override
    public String description() {
        return description;
    }
}
