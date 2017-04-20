package mc.minicraft.engine.entity.particle;

import mc.api.Buffer;
import mc.api.Sound;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.engine.Screen;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.gfx.Color;

import java.io.IOException;

public final class TextParticle extends Entity {
    private String msg;
    private int col;
    private int time = 0;
    public double xa, ya, za;
    public double xx, yy, zz;

    public TextParticle(Sound sound, String msg, int x, int y, int col) {
        super(sound, EntityType.TEXT_PARTICLE);
        this.msg = msg;
        this.x = x;
        this.y = y;
        this.col = col;
        xx = x;
        yy = y;
        zz = 2;
        xa = random.nextGaussian() * 0.3;
        ya = random.nextGaussian() * 0.2;
        za = random.nextFloat() * 0.7 + 2;
    }

    public void tick() {
        time++;
        if (time > 60) {
            remove();
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
        x = (int) xx;
        y = (int) yy;
    }

    @Override
    public void write(Buffer.Output output) throws IOException {
        super.write(output);
        output.writeString(msg);

        output.writeVarInt(col);
        output.writeVarInt(time);

        output.writeDouble(xa);
        output.writeDouble(ya);
        output.writeDouble(za);

        output.writeDouble(xx);
        output.writeDouble(yy);
        output.writeDouble(zz);
    }

    @Override
    protected void read(Buffer.Input input) throws IOException {
        super.read(input);
        msg = input.readString();
        col = input.readVarInt();
        time = input.readVarInt();

        xa = input.readDouble();
        ya = input.readDouble();
        za = input.readDouble();

        xx = input.readDouble();
        yy = input.readDouble();
        zz = input.readDouble();
    }

    public void render(Screen screen) {
//		Font.draw(msg, screen, x - msg.length() * 4, y, Color.get(-1, 0, 0, 0));
        screen.draw(msg, x - msg.length() * 4 + 1, y - (int) (zz) + 1, Color.get(-1, 0, 0, 0));
        screen.draw(msg, x - msg.length() * 4, y - (int) (zz), col);
    }

}
