package mc.minicraft.engine.entity;

import mc.api.Buffer;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.Item;

import java.io.IOException;

public final class ItemEntity extends Entity {
    private int lifeTime;
    protected int walkDist = 0;
    protected int dir = 0;
    public int hurtTime = 0;
    protected int xKnockback, yKnockback;
    public double xa, ya, za;
    public double xx, yy, zz;
    public Item item;
    private int time = 0;

    private final PlayerHandler handler;
    private final PropertyReader reader;

    public ItemEntity(Sound sound, PlayerHandler handler, PropertyReader reader, Item item, int x, int y) {
        super(sound, EntityType.ITEM_ENTITY);
        this.handler = handler;
        this.reader = reader;
        this.item = item;
        xx = this.x = x;
        yy = this.y = y;
        xr = 3;
        yr = 3;

        zz = 2;
        xa = random.nextGaussian() * 0.3;
        ya = random.nextGaussian() * 0.2;
        za = random.nextFloat() * 0.7 + 1;

        lifeTime = 60 * 10 + random.nextInt(60);
    }

    @Override
    public void write(Buffer.Output output) throws IOException {
        super.write(output);
        output.writeVarInt(lifeTime);
        output.writeVarInt(walkDist);
        output.writeVarInt(dir);
        output.writeVarInt(hurtTime);
        output.writeVarInt(xKnockback);
        output.writeVarInt(yKnockback);
        output.writeDouble(xa);
        output.writeDouble(ya);
        output.writeDouble(za);
        output.writeDouble(xx);
        output.writeDouble(yy);
        output.writeDouble(zz);
        if (item != null) {
            output.writeBoolean(true);
            item.write(output);
        } else {
            output.writeBoolean(false);
        }
        output.writeVarInt(time);
    }

    @Override
    protected void read(Buffer.Input input) throws IOException {
        super.read(input);
        lifeTime = input.readVarInt();
        walkDist = input.readVarInt();
        dir = input.readVarInt();
        hurtTime = input.readVarInt();
        xKnockback = input.readVarInt();
        yKnockback = input.readVarInt();
        xa = input.readDouble();
        ya = input.readDouble();
        za = input.readDouble();
        xx = input.readDouble();
        yy = input.readDouble();
        zz = input.readDouble();
        boolean itemPresent = input.readBoolean();
        if (itemPresent) {
            item = Item.readItem(sound, handler, reader, input);
        }
        time = input.readVarInt();
    }

    public void tick() {
        time++;
        if (time >= lifeTime) {
            remove();
            return;
        }
        xx += xa;
        yy += ya;
        zz += za;
        if (zz < 0) {
            zz = 0;
            za *= -0.5;
            xa *= 0.6;
            ya *= 0.6;
        }
        za -= 0.15;
        int ox = x;
        int oy = y;
        int nx = (int) xx;
        int ny = (int) yy;
        int expectedx = nx - x;
        int expectedy = ny - y;
        move(nx - x, ny - y);
        int gotx = x - ox;
        int goty = y - oy;
        xx += gotx - expectedx;
        yy += goty - expectedy;

        if (hurtTime > 0) hurtTime--;
    }

    public boolean isBlockableBy(Mob mob) {
        return false;
    }

    public void render(Screen screen) {
        if (time >= lifeTime - 6 * 20) {
            if (time / 6 % 2 == 0) return;
        }
        screen.render(x - 4, y - 4, item.getSprite(), Color.get(-1, 0, 0, 0), 0);
        screen.render(x - 4, y - 4 - (int) (zz), item.getSprite(), item.getColor(), 0);
    }

    protected void touchedBy(Entity entity) {
        if (time > 30) entity.touchItem(this);
    }

    public void take(Player player) {
        sound.play(x, y, Sound.Type.PICKUP);
        player.score++;
        item.onTake(this);
        remove();
    }
}
