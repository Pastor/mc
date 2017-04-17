package mc.minicraft.component.crafting;

import mc.minicraft.component.entity.Player;
import mc.minicraft.component.item.ToolItem;
import mc.minicraft.component.item.ToolType;

public class ToolRecipe extends Recipe {
    private ToolType type;
    private int level;

    public ToolRecipe(ToolType type, int level) {
        super(new ToolItem(type, level));
        this.type = type;
        this.level = level;
    }

    public void craft(Player player) {
        player.inventory.add(0, new ToolItem(type, level));
    }
}
