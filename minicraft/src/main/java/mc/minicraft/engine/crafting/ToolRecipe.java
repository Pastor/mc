package mc.minicraft.engine.crafting;

import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.item.ToolItem;
import mc.minicraft.engine.item.ToolType;

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
