package mc.minecraft.client.crafting;

import mc.minicraft.component.entity.Player;
import mc.minecraft.client.item.ResourceItem;
import mc.minecraft.client.item.resource.Resource;

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
