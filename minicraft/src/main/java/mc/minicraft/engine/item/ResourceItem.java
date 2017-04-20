package mc.minicraft.engine.item;

import mc.api.Buffer;
import mc.minicraft.data.game.entity.ItemType;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.ItemEntity;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.resource.Resource;
import mc.minicraft.engine.level.Level;
import mc.minicraft.engine.level.tile.Tile;

import java.io.IOException;

public final class ResourceItem extends Item {
    public Resource resource;
    public int count = 1;

    public ResourceItem(Resource resource) {
        super(ItemType.RESOURCE_ITEM);
        this.resource = resource;
        this.color = resource.color;
        this.sprite = resource.sprite;
        this.name = resource.name;
    }

    public ResourceItem(Resource resource, int count) {
        super(ItemType.RESOURCE_ITEM);
        this.resource = resource;
        this.color = resource.color;
        this.sprite = resource.sprite;
        this.name = resource.name;
        this.count = count;
    }

    @Override
    public void write(Buffer.Output output) throws IOException {
        super.write(output);
        output.writeVarInt(count);
    }

    @Override
    protected void read(Buffer.Input input) throws IOException {
        super.read(input);
        count = input.readVarInt();
        resource = new Resource(name, sprite, color);
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.render(x, y, sprite, color, 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.render(x, y, sprite, color, 0);
        screen.draw(name, x + 32, y, Color.get(-1, 555, 555, 555));
        int cc = count;
        if (cc > 999) cc = 999;
        screen.draw("" + cc, x + 8, y, Color.get(-1, 444, 444, 444));
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        if (resource.interactOn(tile, level, xt, yt, player, attackDir)) {
            count--;
            return true;
        }
        return false;
    }

    public boolean isDepleted() {
        return count <= 0;
    }

}