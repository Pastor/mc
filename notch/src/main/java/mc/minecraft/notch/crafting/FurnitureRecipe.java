package mc.minecraft.notch.crafting;

import mc.minecraft.notch.entity.Furniture;
import mc.minecraft.notch.entity.Player;
import mc.minecraft.notch.item.FurnitureItem;

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
