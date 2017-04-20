package mc.minicraft.engine.item;

import mc.api.Buffer;
import mc.minicraft.data.game.entity.ItemType;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.ItemEntity;
import mc.minicraft.engine.gfx.Color;

import java.io.IOException;
import java.util.Random;

public final class ToolItem extends Item {
    private final Random random = new Random();

    public static final int MAX_LEVEL = 5;
    public static final String[] LEVEL_NAMES = { //
            "Wood", "Rock", "Iron", "Gold", "Gem"//
    };

    public static final int[] LEVEL_COLORS = {//
            Color.get(-1, 100, 321, 431),//
            Color.get(-1, 100, 321, 111),//
            Color.get(-1, 100, 321, 555),//
            Color.get(-1, 100, 321, 550),//
            Color.get(-1, 100, 321, 055),//
    };

    public ToolType type;
    public int level = 0;

    public ToolItem(ToolType type, int level) {
        super(ItemType.TOOL_ITEM);
        this.type = type;
        this.level = level;
        this.color = LEVEL_COLORS[level];
        this.sprite = type.sprite + 5 * 32;
        this.name = LEVEL_NAMES[level] + " " + type.name;
    }

    @Override
    public void write(Buffer.Output output) throws IOException {
        super.write(output);
        output.writeVarInt(level);
        output.writeString(name);
    }

    @Override
    protected void read(Buffer.Input input) throws IOException {
        super.read(input);
        level = input.readVarInt();
        name = input.readString();
        type = ToolType.find(name);
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
        screen.draw(getName(), x + 8, y, Color.get(-1, 555, 555, 555));
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public boolean canAttack() {
        return true;
    }

    public int getAttackDamageBonus(Entity e) {
        if (type == ToolType.axe) {
            return (level + 1) * 2 + random.nextInt(4);
        }
        if (type == ToolType.sword) {
            return (level + 1) * 3 + random.nextInt(2 + level * level * 2);
        }
        return 1;
    }

    public boolean matches(Item item) {
        if (item instanceof ToolItem) {
            ToolItem other = (ToolItem) item;
            if (other.type != type) return false;
            if (other.level != level) return false;
            return true;
        }
        return false;
    }
}