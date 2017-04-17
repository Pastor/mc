package mc.minicraft.component.crafting;

import mc.minicraft.component.entity.Player;
import mc.minicraft.component.item.ResourceItem;
import mc.minicraft.component.item.resource.Resource;

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
