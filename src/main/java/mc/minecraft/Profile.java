package mc.minecraft;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Profile {
    private final Set<Property> properties = new HashSet<>();
    public final String name;
    public final String id;

    public Profile(UUID uuid, String player) {
        this.name = player;
        this.id = uuid == null ? null : uuid.toString();
    }

    public Profile(String uuid, String name) {
        this.name = name;
        this.id = uuid;
    }

    public Set<Property> properties() {
        return properties;
    }

    public UUID uuid() {
        return UUID.fromString(id);
    }

    public static final class Property {
        public final String name;
        public final String value;
        public final String signature;

        public Property(String propertyName, String value, String signature) {
            this.name = propertyName;
            this.value = value;
            this.signature = signature;
        }

        public boolean hasSignature() {
            return signature != null && !signature.isEmpty();
        }
    }

    @Override
    public String toString() {
        return Util.toString(this);
    }
}
