package mc.engine.property;

public interface Property {

    enum Type {
        STRING {
            @Override
            Object convert(Object value) {
                if (!(value instanceof String)) {
                    return value.toString();
                }
                return value;
            }
        },
        INTEGER {
            @Override
            Object convert(Object value) {
                if (value instanceof String)
                    return Integer.valueOf((String) value);
                return value;
            }
        },
        BOOLEAN {
            @Override
            Object convert(Object value) {
                if (value instanceof String)
                    return Boolean.valueOf((String) value);
                return value;
            }
        };

        abstract Object convert(Object value);
    }

    Type type();

    String key();

    String description();

    Object value();

    <E> E asValue();

    String asString();

}
