package mc.minicraft.engine.entity;

import mc.api.Buffer;
import mc.api.Sound;
import mc.engine.property.Property;
import mc.engine.property.PropertyConstants;
import mc.engine.property.PropertyContainer;
import mc.engine.property.PropertyReader;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.particle.TextParticle;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.item.FurnitureItem;
import mc.minicraft.engine.item.Item;
import mc.minicraft.engine.item.PowerGloveItem;
import mc.minicraft.engine.level.BaseLevel;
import mc.minicraft.engine.level.tile.Tile;

import java.awt.*;
import java.io.IOException;
import java.util.Set;

public final class Player extends Mob implements PropertyContainer.Listener {
    private static final int LIGHT_RADIUS = 10;
    public final PropertyReader property;
    private int attackTime, attackDir;

    public final PlayerHandler handler;
    public Inventory inventory = new Inventory();
    public Item attackItem;
    public Item activeItem;
    public int stamina;
    public int staminaRecharge;
    public int staminaRechargeDelay;
    public int score;
    public int maxStamina = 10;
    private int onStairDelay;
    public int invulnerableTime = 0;
    public boolean die = false;

    public Player(Sound sound, PlayerHandler handler, PropertyReader property) {
        super(sound, EntityType.PLAYER);
        this.property = property;
        this.handler = handler;
        x = 24;
        y = 24;
        stamina = maxStamina = property.property(PropertyConstants.PLAYER_STAMINA).asValue();
        health = maxHealth = property.property(PropertyConstants.PLAYER_HEALTH).asValue();

        inventory.add(new FurnitureItem(new Workbench(sound, handler, property), sound, handler, property));
        inventory.add(new PowerGloveItem());
        property.addListener(this);
    }

    @Override
    public void write(Buffer.Output output) throws IOException {
        super.write(output);
        output.writeVarInt(attackTime);
        output.writeVarInt(attackDir);
        if (attackItem != null) {
            output.writeBoolean(true);
            attackItem.write(output);
        } else {
            output.writeBoolean(false);
        }
        if (activeItem != null) {
            output.writeBoolean(true);
            activeItem.write(output);
        } else {
            output.writeBoolean(false);
        }
        output.writeVarInt(stamina);
        output.writeVarInt(staminaRecharge);
        output.writeVarInt(staminaRechargeDelay);
        output.writeVarInt(score);
        output.writeVarInt(maxHealth);
        output.writeVarInt(onStairDelay);
        output.writeVarInt(invulnerableTime);
        output.writeBoolean(die);
        inventory.write(output);
    }

    @Override
    protected void read(Buffer.Input input) throws IOException {
        super.read(input);
        attackTime = input.readVarInt();
        attackDir = input.readVarInt();
        boolean attackItemPresent = input.readBoolean();
        if (attackItemPresent) {
            attackItem = Item.readItem(sound, handler, property, input);
        }
        boolean activeItemPresent = input.readBoolean();
        if (activeItemPresent) {
            activeItem = Item.readItem(sound, handler, property, input);
        }
        stamina = input.readVarInt();
        staminaRecharge = input.readVarInt();
        staminaRechargeDelay = input.readVarInt();
        score = input.readVarInt();
        maxHealth = input.readVarInt();
        onStairDelay = input.readVarInt();
        invulnerableTime = input.readVarInt();
        die = input.readBoolean();
        inventory.read(sound, handler, property, input);
    }

    public void tick() {
        if (die) {
            remove();
            return;
        }
        super.tick();

        if (invulnerableTime > 0) invulnerableTime--;
        if (level != null) {
            Tile onTile = level.getTile(x >> 4, y >> 4);
            if (onTile == Tile.stairsDown || onTile == Tile.stairsUp) {
                if (onStairDelay == 0) {
                    changeLevel((onTile == Tile.stairsUp) ? 1 : -1);
                    onStairDelay = 10;
                    return;
                }
                onStairDelay = 10;
            } else {
                if (onStairDelay > 0) onStairDelay--;
            }
        }

        if (stamina <= 0 && staminaRechargeDelay == 0 && staminaRecharge == 0) {
            staminaRechargeDelay = 40;
        }

        if (staminaRechargeDelay > 0) {
            staminaRechargeDelay--;
        }

        if (staminaRechargeDelay == 0) {
            staminaRecharge++;
            if (isSwimming()) {
                staminaRecharge = 0;
            }
            while (staminaRecharge > 10) {
                staminaRecharge -= 10;
                if (stamina < maxStamina) stamina++;
            }
        }

        Point move = handler.move();
        int xa = move.x;
        int ya = move.y;


        if (isSwimming() && tickTime % 60 == 0) {
            if (stamina > 0) {
                stamina--;
            } else {
                hurt(this, 1, dir ^ 1);
            }
        }

        if (staminaRechargeDelay % 2 == 0) {
            move(xa, ya);
        }

        if (ya != 0 || xa != 0) {
            System.out.println(
                    String.format("Player[%20s]                , X: %5d, Y: %5d, Dist: %5d, Attack: %s, Item: %10s",
                            id.toString().toUpperCase(), x, y,
                            walkDist, handler.isAttacked(), activeItem == null ? "none" : activeItem.getName()));
        }

        if (handler.isAttacked()) {
            if (stamina == 0) {

            } else {
                stamina--;
                staminaRecharge = 0;
                attack();
            }
        }
        if (attackTime > 0)
            attackTime--;
    }

    public void tickMenu() {
        if (handler.isMenuClicked()) {
            if (!use()) {
                handler.inventoryMenu(this);
            }
        } else if (handler.escapePressed()) {
            handler.mainMenu(this);
        }
    }

    private boolean use() {
        int yo = -2;
        if (dir == 0 && use(x - 8, y + 4 + yo, x + 8, y + 12 + yo)) return true;
        if (dir == 1 && use(x - 8, y - 12 + yo, x + 8, y - 4 + yo)) return true;
        if (dir == 3 && use(x + 4, y - 8 + yo, x + 12, y + 8 + yo)) return true;
        if (dir == 2 && use(x - 12, y - 8 + yo, x - 4, y + 8 + yo)) return true;

        int xt = x >> 4;
        int yt = (y + yo) >> 4;
        int r = 12;
        if (attackDir == 0) yt = (y + r + yo) >> 4;
        if (attackDir == 1) yt = (y - r + yo) >> 4;
        if (attackDir == 2) xt = (x - r) >> 4;
        if (attackDir == 3) xt = (x + r) >> 4;

        if (xt >= 0 && yt >= 0 && xt < level.w && yt < level.h) {
            if (level.getTile(xt, yt).use(level, xt, yt, this, attackDir)) return true;
        }

        return false;
    }

    private void attack() {
        walkDist += 8;
        attackDir = dir;
        attackItem = activeItem;
        boolean done = false;

        if (activeItem != null) {
            attackTime = 10;
            int yo = -2;
            int range = 12;
            if (dir == 0 && interact(x - 8, y + 4 + yo, x + 8, y + range + yo)) done = true;
            if (dir == 1 && interact(x - 8, y - range + yo, x + 8, y - 4 + yo)) done = true;
            if (dir == 3 && interact(x + 4, y - 8 + yo, x + range, y + 8 + yo)) done = true;
            if (dir == 2 && interact(x - range, y - 8 + yo, x - 4, y + 8 + yo)) done = true;
            if (done) return;

            int xt = x >> 4;
            int yt = (y + yo) >> 4;
            int r = 12;
            if (attackDir == 0) yt = (y + r + yo) >> 4;
            if (attackDir == 1) yt = (y - r + yo) >> 4;
            if (attackDir == 2) xt = (x - r) >> 4;
            if (attackDir == 3) xt = (x + r) >> 4;

            if (xt >= 0 && yt >= 0 && xt < level.w && yt < level.h) {
                if (activeItem.interactOn(level.getTile(xt, yt), level, xt, yt, this, attackDir)) {
                    done = true;
                } else {
                    if (level.getTile(xt, yt).interact(level, xt, yt, this, activeItem, attackDir)) {
                        done = true;
                    }
                }
                if (activeItem.isDepleted()) {
                    activeItem = null;
                }
            }
        }

        if (done) return;

        if (activeItem == null || activeItem.canAttack()) {
            attackTime = 5;
            int yo = -2;
            int range = 20;
            if (dir == 0) hurt(x - 8, y + 4 + yo, x + 8, y + range + yo);
            if (dir == 1) hurt(x - 8, y - range + yo, x + 8, y - 4 + yo);
            if (dir == 3) hurt(x + 4, y - 8 + yo, x + range, y + 8 + yo);
            if (dir == 2) hurt(x - range, y - 8 + yo, x - 4, y + 8 + yo);

            int xt = x >> 4;
            int yt = (y + yo) >> 4;
            int r = 12;
            if (attackDir == 0) yt = (y + r + yo) >> 4;
            if (attackDir == 1) yt = (y - r + yo) >> 4;
            if (attackDir == 2) xt = (x - r) >> 4;
            if (attackDir == 3) xt = (x + r) >> 4;

            if (xt >= 0 && yt >= 0 && xt < level.w && yt < level.h) {
                level.getTile(xt, yt).hurt(level, xt, yt, this, random.nextInt(3) + 1, attackDir);
            }
        }

    }

    private boolean use(int x0, int y0, int x1, int y1) {
        Set<Entity> entities = level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) if (e.use(this, attackDir)) return true;
        }
        return false;
    }

    private boolean interact(int x0, int y0, int x1, int y1) {
        Set<Entity> entities = level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) if (e.interact(this, activeItem, attackDir)) return true;
        }
        return false;
    }

    private void hurt(int x0, int y0, int x1, int y1) {
        Set<Entity> entities = level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) e.hurt(this, getAttackDamage(e), attackDir);
        }
    }

    private int getAttackDamage(Entity e) {
        int dmg = random.nextInt(3) + 1;
        if (attackItem != null) {
            dmg += attackItem.getAttackDamageBonus(e);
        }
        return dmg;
    }

    public void render(Screen screen) {
        int xt = 0;
        int yt = 14;

        int flip1 = (walkDist >> 3) & 1;
        int flip2 = (walkDist >> 3) & 1;

        if (dir == 1) {
            xt += 2;
        }
        if (dir > 1) {
            flip1 = 0;
            flip2 = ((walkDist >> 4) & 1);
            if (dir == 2) {
                flip1 = 1;
            }
            xt += 4 + ((walkDist >> 3) & 1) * 2;
        }

        int xo = x - 8;
        int yo = y - 11;
        if (isSwimming()) {
            yo += 4;
            int waterColor = Color.get(-1, -1, 115, 335);
            if (tickTime / 8 % 2 == 0) {
                waterColor = Color.get(-1, 335, 5, 115);
            }
            screen.render(xo + 0, yo + 3, 5 + 13 * 32, waterColor, 0);
            screen.render(xo + 8, yo + 3, 5 + 13 * 32, waterColor, 1);
        }

        if (attackTime > 0 && attackDir == 1) {
            screen.render(xo + 0, yo - 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 0);
            screen.render(xo + 8, yo - 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 1);
            if (attackItem != null) {
                attackItem.renderIcon(screen, xo + 4, yo - 4);
            }
        }
        int col = Color.get(-1, 100, 220, 532);
        if (hurtTime > 0) {
            col = Color.get(-1, 555, 555, 555);
        }

        if (activeItem instanceof FurnitureItem) {
            yt += 2;
        }
        screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col, flip1);
        screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col, flip1);
        if (!isSwimming()) {
            screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2);
            screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2);
        }

        if (attackTime > 0 && attackDir == 2) {
            screen.render(xo - 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 1);
            screen.render(xo - 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 3);
            if (attackItem != null) {
                attackItem.renderIcon(screen, xo - 4, yo + 4);
            }
        }
        if (attackTime > 0 && attackDir == 3) {
            screen.render(xo + 8 + 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 0);
            screen.render(xo + 8 + 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 2);
            if (attackItem != null) {
                attackItem.renderIcon(screen, xo + 8 + 4, yo + 4);
            }
        }
        if (attackTime > 0 && attackDir == 0) {
            screen.render(xo + 0, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 2);
            screen.render(xo + 8, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 3);
            if (attackItem != null) {
                attackItem.renderIcon(screen, xo + 4, yo + 8 + 4);
            }
        }

        if (activeItem instanceof FurnitureItem) {
            Furniture furniture = ((FurnitureItem) activeItem).furniture;
            furniture.x = x;
            furniture.y = yo;
            furniture.render(screen);
        }
    }

    public void touchItem(ItemEntity itemEntity) {
        itemEntity.take(this);
        inventory.add(itemEntity.item);
    }

    public boolean canSwim() {
        return true;
    }

    public boolean findStartPos(BaseLevel level) {
        while (true) {
            int x = random.nextInt(level.w);
            int y = random.nextInt(level.h);
            if (level.getTile(x, y) == Tile.grass) {
                this.x = x * 16 + 8;
                this.y = y * 16 + 8;
                return true;
            }
        }
    }

    public boolean payStamina(int cost) {
        if (cost > stamina) return false;
        stamina -= cost;
        return true;
    }

    public void changeLevel(int dir) {
        handler.scheduleLevelChange(dir);
    }

    public int getLightRadius() {
        int r = LIGHT_RADIUS;
        if (activeItem != null) {
            if (activeItem instanceof FurnitureItem) {
                int rr = ((FurnitureItem) activeItem).furniture.getLightRadius();
                if (rr > r)
                    r = rr;
            }
        }
        return r;
    }

    protected void die() {
        super.die();
        die = true;
        sound.play(x, y, Sound.Type.PLAYER_DEATH);
    }

    protected void touchedBy(Entity entity) {
        if (!(entity instanceof Player)) {
            entity.touchedBy(this);
        }
    }

    protected void doHurt(int damage, int attackDir) {
        if (hurtTime > 0 || invulnerableTime > 0) return;

        sound.play(x, y, Sound.Type.PLAYER_HURT);
        level.add(new TextParticle(sound, "" + damage, x, y, Color.get(-1, 504, 504, 504)));
        health -= damage;
        if (attackDir == 0) yKnockback = +6;
        if (attackDir == 1) yKnockback = -6;
        if (attackDir == 2) xKnockback = -6;
        if (attackDir == 3) xKnockback = +6;
        hurtTime = 10;
        invulnerableTime = 30;
    }

    public void gameWon() {
        if (level.hasPlayer()) {
            level.player().invulnerableTime = 60 * 5;
        }
        handler.won();
    }

    @Override
    public void update(Property value) {
        if (PropertyConstants.PLAYER_STAMINA.equals(value.key())) {
            stamina = maxStamina = value.asValue();
        } else if (PropertyConstants.PLAYER_HEALTH.equals(value.key())) {
            health = maxHealth = value.asValue();
        }

    }
}