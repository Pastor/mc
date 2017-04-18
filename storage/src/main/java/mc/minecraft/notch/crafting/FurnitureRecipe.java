package mc.minecraft.client.crafting;

import mc.minicraft.component.entity.Furniture;
import mc.minicraft.component.entity.Player;
import mc.minecraft.client.item.FurnitureItem;

public class FurnitureRecipe extends Recipe {
    private Class<? extends Furniture> clazz;

    public FurnitureRecipe(Class<? extends Furniture> clazz) throws InstantiationException, IllegalAccessException {
        super(new FurnitureItem(clazz.newInstance()));
        this.clazz = clazz;
    }

    public void craft(Player player) {
        try {
            player.inventory.add(0, new FurnitureItem(clazz.newInstance()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
