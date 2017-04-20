package mc.minicraft.engine.crafting;

import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.engine.entity.Furniture;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.entity.PlayerHandler;
import mc.minicraft.engine.item.FurnitureItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FurnitureRecipe extends Recipe {
    private Class<? extends Furniture> clazz;

    public FurnitureRecipe(Class<? extends Furniture> clazz, Sound sound, PlayerHandler handler, PropertyReader reader)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(new FurnitureItem(newInstance(clazz, sound, handler, reader), sound, handler, reader));
        this.clazz = clazz;
    }

    private static <E extends Furniture> E newInstance(Class<E> clazz,
                                                       Sound sound,
                                                       PlayerHandler handler,
                                                       PropertyReader reader)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<E> constructor = clazz.getDeclaredConstructor(
                Sound.class,
                PlayerHandler.class, PropertyReader.class);
        constructor.setAccessible(true);
        return constructor.newInstance(sound, handler, reader);
    }

    public void craft(Player player) {
        try {
            player.inventory.add(0, new FurnitureItem(clazz.newInstance(), player.sound,
                    player.handler, player.property));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
