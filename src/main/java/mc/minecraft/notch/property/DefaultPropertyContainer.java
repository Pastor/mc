package mc.minecraft.notch.property;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class DefaultPropertyContainer implements PropertyContainer {
    private final List<Listener> listeners = new LinkedList<>();
    private final Map<String, AbstractProperty> properties = new HashMap<>();

    public DefaultPropertyContainer() {
        register(new DefaultProperty(
                PropertyReadonly.Type.BOOLEAN, PropertyConstants.DEVELOPMENT, Boolean.TRUE,
                "Режим разработчика"));
        int scale = 2;
        register(new DefaultProperty(
                PropertyReadonly.Type.INTEGER, PropertyConstants.GAME_SCALE, scale,
                "Критирий отображения компонентов"));
        register(new DefaultProperty(
                PropertyReadonly.Type.INTEGER, PropertyConstants.GAME_WIDTH, 1024,
                "Ширина игрового поля"));
        register(new DefaultProperty(
                PropertyReadonly.Type.INTEGER, PropertyConstants.GAME_HEIGHT, 768,
                "Высота игрового поля"));
        register(new DefaultProperty(
                PropertyReadonly.Type.BOOLEAN, PropertyConstants.SHOW_FPS, Boolean.TRUE,
                "Отображение FPS"));

        register(new DefaultProperty(
                PropertyReadonly.Type.STRING, PropertyConstants.SERVER_HOSTNAME, PropertyConstants.SERVER_HOSTNAME_DEFAULT,
                "Адрес сервера"));
        register(new DefaultProperty(
                PropertyReadonly.Type.INTEGER, PropertyConstants.SERVER_PORT, PropertyConstants.SERVER_PORT_DEFAULT,
                "Порт сервера"));



        register(new DefaultProperty(
                PropertyReadonly.Type.INTEGER, PropertyConstants.PLAYER_STAMINA, 10,
                "Выносливость пользователя"));
        register(new DefaultProperty(
                PropertyReadonly.Type.INTEGER, PropertyConstants.PLAYER_HEALTH, 10,
                "Жизнь пользователя"));
    }

    @Override
    public PropertyReadonly property(String key) {
        return properties.get(key);
    }

    @Override
    public void addListener(Listener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void firePropertyChange(PropertyReadonly value) {
        for (Listener listener : listeners) {
            listener.update(value);
        }
    }

    @Override
    public void update(PropertyReadonly property, Object value) {
        AbstractProperty abstractProperty = properties.get(property.key().toLowerCase());
        if (abstractProperty != null) {
            abstractProperty.setValue(value);
            firePropertyChange(property);
        }
    }

    @Override
    public void register(AbstractProperty property) {
        properties.put(property.key().toLowerCase(), property);
    }
}
