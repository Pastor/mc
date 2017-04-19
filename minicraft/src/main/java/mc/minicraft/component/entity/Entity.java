package mc.minicraft.component.entity;

import mc.api.Buffer;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.Magic;
import mc.minicraft.component.Screen;
import mc.minicraft.component.entity.particle.SmashParticle;
import mc.minicraft.component.entity.particle.TextParticle;
import mc.minicraft.component.item.Item;
import mc.minicraft.component.level.Level;
import mc.minicraft.component.level.tile.Tile;
import mc.minicraft.data.game.entity.EntityType;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Entity {
    public static final AtomicLong counter = new AtomicLong(0);
    protected final Random random = new Random();
    public UUID id;
    public int x, y;
    public int xr = 6;
    public int yr = 6;
    public boolean removed;
    public Level level;
    public final Sound sound;
    public final EntityType type;

    public Entity(Sound sound, EntityType type) {
        this.type = type;
        this.id = UUID.randomUUID();
        this.sound = sound;
    }

    public void write(Buffer.Output output) throws IOException {
        output.writeByte(Magic.value(Integer.class, type()));
        output.writeString(id.toString());
        output.writeVarInt(x);
        output.writeVarInt(y);
        output.writeBoolean(removed);
    }

    protected void read(Buffer.Input input) throws IOException {
        id = UUID.fromString(input.readString());
        x = input.readVarInt();
        y = input.readVarInt();
        removed = input.readBoolean();
    }

    public void render(Screen screen) {
    }

    public void tick() {
    }

    public void remove() {
        removed = true;
    }

    public final void init(Level level) {
        this.level = level;
    }

    public boolean intersects(int x0, int y0, int x1, int y1) {
        return !(x + xr < x0 || y + yr < y0 || x - xr > x1 || y - yr > y1);
    }

    public boolean blocks(Entity e) {
        return false;
    }

    public void hurt(Mob mob, int dmg, int attackDir) {
    }

    public void hurt(Tile tile, int x, int y, int dmg) {
    }

    public boolean move(int xa, int ya) {
        if (xa != 0 || ya != 0) {
            boolean stopped = true;
            if (xa != 0 && move2(xa, 0)) stopped = false;
            if (ya != 0 && move2(0, ya)) stopped = false;
            if (!stopped) {
                int xt = x >> 4;
                int yt = y >> 4;
                level.getTile(xt, yt).steppedOn(level, xt, yt, this);
            }
            return !stopped;
        }
        return true;
    }

    protected boolean move2(int xa, int ya) {
        if (xa != 0 && ya != 0) throw new IllegalArgumentException("Move2 can only move along one axis at a time!");

        int xto0 = ((x) - xr) >> 4;
        int yto0 = ((y) - yr) >> 4;
        int xto1 = ((x) + xr) >> 4;
        int yto1 = ((y) + yr) >> 4;

        int xt0 = ((x + xa) - xr) >> 4;
        int yt0 = ((y + ya) - yr) >> 4;
        int xt1 = ((x + xa) + xr) >> 4;
        int yt1 = ((y + ya) + yr) >> 4;
        boolean blocked = false;
        for (int yt = yt0; yt <= yt1; yt++)
            for (int xt = xt0; xt <= xt1; xt++) {
                if (xt >= xto0 && xt <= xto1 && yt >= yto0 && yt <= yto1) continue;
                level.getTile(xt, yt).bumpedInto(level, xt, yt, this);
                if (!level.getTile(xt, yt).mayPass(level, xt, yt, this)) {
                    blocked = true;
                    return false;
                }
            }
        if (blocked) return false;

        Set<Entity> wasInside = level.getEntities(x - xr, y - yr, x + xr, y + yr);
        Set<Entity> isInside = level.getEntities(x + xa - xr, y + ya - yr, x + xa + xr, y + ya + yr);
        for (Entity e : isInside) {
            if (e == this) continue;

            e.touchedBy(this);
        }
        isInside.removeAll(wasInside);
        for (Entity e : isInside) {
            if (e == this) continue;

            if (e.blocks(this)) {
                return false;
            }
        }

        x += xa;
        y += ya;
        return true;
    }

    protected void touchedBy(Entity entity) {
    }

    public boolean isBlockableBy(Mob mob) {
        return true;
    }

    public void touchItem(ItemEntity itemEntity) {
    }

    public boolean canSwim() {
        return false;
    }

    public boolean interact(Player player, Item item, int attackDir) {
        return item.interact(player, this, attackDir);
    }

    public boolean use(Player player, int attackDir) {
        return false;
    }

    public int getLightRadius() {
        return 0;
    }

    public final EntityType type() {
        return type;
    }

    private static EntityType readType(Buffer.Input in) throws IOException {
        return Magic.key(EntityType.class, in.readByte());
    }

    public static Entity readEntity(Sound sound, PlayerHandler handler, PropertyReader reader, Buffer.Input input) throws IOException {
        EntityType type = readType(input);
        final Entity entity;
        switch (type) {
            case SMASH_PARTICLE:
                entity = new SmashParticle(sound, 0, 0);
                break;
            case TEXT_PARTICLE:
                entity = new TextParticle(sound, null, 0, 0, 0);
                break;
            case AIR_WIZARD:
                entity = new AirWizard(sound);
                break;
            case ANVIL:
                entity = new Anvil(sound, handler, reader);
                break;
            case CHEST:
                entity = new Chest(sound, handler, reader);
                break;
            case FURNACE:
                entity = new Furnace(sound, handler, reader);
                break;
            case LANTERN:
                entity = new Lantern(sound, handler, reader);
                break;
            case OVEN:
                entity = new Oven(sound, handler, reader);
                break;
            case PLAYER:
                entity = new Player(sound, handler, reader);
                break;
            case SLIME:
                entity = new Slime(sound, 0);
                break;
            case SPARK:
                entity = new Spark(sound, new AirWizard(sound), 0, 0);
                break;
            case WORKBENCH:
                entity = new Workbench(sound, handler, reader);
                break;
            case ZOMBIE:
                entity = new Zombie(sound, 0);
                break;
            case ITEM_ENTITY:
                entity = new ItemEntity(sound, handler, reader, null, 0, 0);
                break;
            default:
                throw new IllegalArgumentException();
        }
        entity.read(input);
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]";
    }
}