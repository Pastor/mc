package mc.minicraft.component.entity;

import mc.api.Buffer;
import mc.api.Sound;
import mc.minicraft.component.Screen;
import mc.minicraft.component.gfx.Color;
import mc.minicraft.data.game.entity.EntityType;

import java.io.IOException;
import java.util.Set;

public final class Spark extends Entity {
    private int lifeTime;
    public double xa, ya;
    public double xx, yy;
    private int time;
    private AirWizard owner;

    public Spark(Sound sound, AirWizard owner, double xa, double ya) {
        super(sound, EntityType.SPARK);
        this.owner = owner;
        xx = this.x = owner.x;
        yy = this.y = owner.y;
        xr = 0;
        yr = 0;

        this.xa = xa;
        this.ya = ya;

        lifeTime = 60 * 10 + random.nextInt(30);
    }

    @Override
    public void write(Buffer.Output output) throws IOException {
        super.write(output);
        output.writeVarInt(lifeTime);
        output.writeDouble(xa);
        output.writeDouble(ya);
        output.writeDouble(xx);
        output.writeDouble(yy);
        output.writeVarInt(time);
        owner.write(output);
    }

    @Override
    protected void read(Buffer.Input input) throws IOException {
        super.read(input);
        lifeTime = input.readVarInt();
        xa = input.readDouble();
        ya = input.readDouble();
        xx = input.readDouble();
        yy = input.readDouble();
        time = input.readVarInt();
        owner.read(input);
    }

    public void tick() {
        time++;
        if (time >= lifeTime) {
            remove();
            return;
        }
        xx += xa;
        yy += ya;
        x = (int) xx;
        y = (int) yy;
        Set<Entity> toHit = level.getEntities(x, y, x, y);
        toHit.stream().filter(e -> e instanceof Mob && !(e instanceof AirWizard)).forEach(e -> {
            e.hurt(owner, 1, ((Mob) e).dir ^ 1);
        });
    }

    public boolean isBlockableBy(Mob mob) {
        return false;
    }

    public void render(Screen screen) {
        if (time >= lifeTime - 6 * 20) {
            if (time / 6 % 2 == 0) return;
        }

        int xt = 8;
        int yt = 13;

        screen.render(x - 4, y - 4 - 2, xt + yt * 32, Color.get(-1, 555, 555, 555), random.nextInt(4));
        screen.render(x - 4, y - 4 + 2, xt + yt * 32, Color.get(-1, 000, 000, 000), random.nextInt(4));
    }
}
