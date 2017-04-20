package mc.minicraft.engine.item;

import mc.api.Buffer;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.Magic;
import mc.minicraft.data.game.entity.ItemType;
import mc.minicraft.engine.ListItem;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.*;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.Level;
import mc.minicraft.engine.level.tile.Tile;

import java.io.IOException;

public abstract class Item implements ListItem {
    protected String name;
    protected int color;
    protected int sprite;
    private final ItemType type;

    protected Item(ItemType type) {
        this.type = type;
    }

    public void write(Buffer.Output output) throws IOException {
        output.writeByte(Magic.value(Integer.class, type));
        output.writeString(name);
        output.writeVarInt(color);
        output.writeVarInt(sprite);
    }

    protected void read(Buffer.Input input) throws IOException {
        name = input.readString();
        color = input.readVarInt();
        sprite = input.readVarInt();
    }

    private static ItemType readType(Buffer.Input in) throws IOException {
        return Magic.key(ItemType.class, in.readByte());
    }

    public static Item readItem(Sound sound, PlayerHandler handler, PropertyReader reader, Buffer.Input input)
            throws IOException {
        ItemType type = readType(input);
        final Item item;
        switch (type) {
            case FURNITURE:
                item = new FurnitureItem(new Workbench(sound, handler, reader), sound, handler, reader);
                break;
            case POWER_GLOVE:
                item = new PowerGloveItem();
                break;
            case RESOURCE_ITEM:
                item = new ResourceItem(Resource.apple);
                break;
            case TOOL_ITEM:
                item = new ToolItem(ToolType.axe, 0);
                break;
            default:
                throw new IllegalArgumentException();
        }
        item.read(input);
        return item;
    }

    public int getColor() {
        return color;
    }

    public int getSprite() {
        return sprite;
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public void renderInventory(Screen screen, int x, int y) {
    }

    public boolean interact(Player player, Entity entity, int attackDir) {
        return false;
    }

    public void renderIcon(Screen screen, int x, int y) {
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        return false;
    }

    public boolean isDepleted() {
        return false;
    }

    public boolean canAttack() {
        return false;
    }

    public int getAttackDamageBonus(Entity e) {
        return 0;
    }

    public String getName() {
        return name;
    }

    public boolean matches(Item item) {
        return item.getClass() == getClass();
    }
}