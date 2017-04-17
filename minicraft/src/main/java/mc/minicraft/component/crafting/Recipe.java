package mc.minicraft.component.crafting;

import mc.minicraft.component.ListItem;
import mc.minicraft.component.Screen;
import mc.minicraft.component.entity.Player;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.component.item.Item;
import mc.minicraft.component.item.ResourceItem;
import mc.minicraft.component.item.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public abstract class Recipe implements ListItem {
    public List<Item> costs = new ArrayList<Item>();
    public boolean canCraft = false;
    public Item resultTemplate;

    public Recipe(Item resultTemplate) {
        this.resultTemplate = resultTemplate;
    }

    public Recipe addCost(Resource resource, int count) {
        costs.add(new ResourceItem(resource, count));
        return this;
    }

    public void checkCanCraft(Player player) {
        for (int i = 0; i < costs.size(); i++) {
            Item item = costs.get(i);
            if (item instanceof ResourceItem) {
                ResourceItem ri = (ResourceItem) item;
                if (!player.inventory.hasResources(ri.resource, ri.count)) {
                    canCraft = false;
                    return;
                }
            }
        }
        canCraft = true;
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.render(x, y, resultTemplate.getSprite(), resultTemplate.getColor(), 0);
        int textColor = canCraft ? Color.get(-1, 555, 555, 555) : Color.get(-1, 222, 222, 222);
        screen.draw(resultTemplate.getName(), x + 8, y, textColor);
    }

    public abstract void craft(Player player);

    public void deductCost(Player player) {
        for (int i = 0; i < costs.size(); i++) {
            Item item = costs.get(i);
            if (item instanceof ResourceItem) {
                ResourceItem ri = (ResourceItem) item;
                player.inventory.removeResource(ri.resource, ri.count);
            }
        }
    }
}