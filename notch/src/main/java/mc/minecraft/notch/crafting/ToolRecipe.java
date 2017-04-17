package mc.minecraft.notch.crafting;


import mc.minecraft.notch.entity.Player;
import mc.minecraft.notch.item.ToolItem;
import mc.minecraft.notch.item.ToolType;

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
