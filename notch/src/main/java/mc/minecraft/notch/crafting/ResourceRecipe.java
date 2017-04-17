package mc.minecraft.notch.crafting;

import mc.minecraft.notch.entity.Player;
import mc.minecraft.notch.item.ResourceItem;
import mc.minecraft.notch.item.resource.Resource;

public class ResourceRecipe extends Recipe {
    private Resource resource;

    public ResourceRecipe(Resource resource) {
        super(new ResourceItem(resource, 1));
        this.resource = resource;
    }

    public void craft(Player player) {
        player.inventory.add(0, new ResourceItem(resource, 1));
    }
}
