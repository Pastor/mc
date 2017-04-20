package mc.minicraft.engine.item;

import mc.api.Buffer;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.ItemType;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.*;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.level.BaseLevel;
import mc.minicraft.engine.level.tile.Tile;

import java.io.IOException;

public class FurnitureItem extends Item {
    public Furniture furniture;
    private Sound sound;
    private PlayerHandler handler;
    private PropertyReader reader;
    public boolean placed = false;

    public FurnitureItem(Furniture furniture, Sound sound, PlayerHandler handler, PropertyReader reader) {
        super(ItemType.FURNITURE);
        this.furniture = furniture;
        this.sound = sound;
        this.handler = handler;
        this.reader = reader;
        this.color = furniture.col;
        this.sprite = furniture.sprite;
        this.name = furniture.name;
    }

    @Override
    public void write(Buffer.Output output) throws IOException {
        super.write(output);
        furniture.write(output);
    }

    @Override
    protected void read(Buffer.Input input) throws IOException {
        super.read(input);
        furniture = (Furniture) Entity.readEntity(sound, handler, reader, input);
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
        screen.draw(name, x + 8, y, Color.get(-1, 555, 555, 555));
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public boolean canAttack() {
        return false;
    }

    public boolean interactOn(Tile tile, BaseLevel level, int xt, int yt, Player player, int attackDir) {
        if (tile.mayPass(level, xt, yt, furniture)) {
            furniture.x = xt * 16 + 8;
            furniture.y = yt * 16 + 8;
            level.add(furniture);
            placed = true;
            return true;
        }
        return false;
    }

    public boolean isDepleted() {
        return placed;
    }
}