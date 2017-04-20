package mc.minicraft.engine.entity;

import mc.api.Buffer;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.item.FurnitureItem;
import mc.minicraft.engine.item.PowerGloveItem;

import java.io.IOException;

public abstract class Furniture extends Entity {
    private int pushTime = 0;
    private int pushDir = -1;
    public int col, sprite;
    public String name;
    private Player shouldTake;
    protected final PlayerHandler handler;
    protected final PropertyReader reader;

    public Furniture(Sound sound, PlayerHandler handler, PropertyReader reader, String name, EntityType type) {
        super(sound, type);
        this.handler = handler;
        this.reader = reader;
        this.name = name;
        xr = 3;
        yr = 3;
    }

    @Override
    public void write(Buffer.Output output) throws IOException {
        super.write(output);
        output.writeVarInt(pushTime);
        output.writeVarInt(pushDir);
        output.writeVarInt(col);
        output.writeVarInt(sprite);
        output.writeString(name);
    }

    @Override
    public void read(Buffer.Input input) throws IOException {
        super.read(input);
        pushTime = input.readVarInt();
        pushDir = input.readVarInt();
        col = input.readVarInt();
        sprite = input.readVarInt();
        name = input.readString();
    }

    public void tick() {
        if (shouldTake != null) {
            if (shouldTake.activeItem instanceof PowerGloveItem) {
                remove();
                shouldTake.inventory.add(0, shouldTake.activeItem);
                shouldTake.activeItem = new FurnitureItem(this, sound, handler, reader);
            }
            shouldTake = null;
        }
        if (pushDir == 0) move(0, +1);
        if (pushDir == 1) move(0, -1);
        if (pushDir == 2) move(-1, 0);
        if (pushDir == 3) move(+1, 0);
        pushDir = -1;
        if (pushTime > 0) pushTime--;
    }

    public void render(Screen screen) {
        screen.render(x - 8, y - 8 - 4, sprite * 2 + 8 * 32, col, 0);
        screen.render(x - 0, y - 8 - 4, sprite * 2 + 8 * 32 + 1, col, 0);
        screen.render(x - 8, y - 0 - 4, sprite * 2 + 8 * 32 + 32, col, 0);
        screen.render(x - 0, y - 0 - 4, sprite * 2 + 8 * 32 + 33, col, 0);
    }

    public boolean blocks(Entity e) {
        return true;
    }

    protected void touchedBy(Entity entity) {
        if (entity instanceof Player && pushTime == 0) {
            pushDir = ((Player) entity).dir;
            pushTime = 10;
        }
    }

    public void take(Player player) {
        shouldTake = player;
    }
}