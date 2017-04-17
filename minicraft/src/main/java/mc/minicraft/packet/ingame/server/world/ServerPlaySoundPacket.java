package mc.minicraft.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.world.sound.BuiltinSound;
import mc.minicraft.data.game.world.sound.CustomSound;
import mc.minicraft.data.game.world.sound.Sound;
import mc.minicraft.data.game.world.sound.SoundCategory;

import java.io.IOException;

public class ServerPlaySoundPacket implements Packet {

    private Sound sound;
    private SoundCategory category;
    private double x;
    private double y;
    private double z;
    private float volume;
    private float pitch;

    @SuppressWarnings("unused")
    private ServerPlaySoundPacket() {
    }

    public ServerPlaySoundPacket(Sound sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {
        this.sound = sound;
        this.category = category;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return this.sound;
    }

    public SoundCategory getCategory() {
        return this.category;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        String value = in.readString();
        try {
            this.sound = mc.minicraft.Magic.key(BuiltinSound.class, value);
        } catch (IllegalArgumentException e) {
            this.sound = new CustomSound(value);
        }

        this.category = mc.minicraft.Magic.key(SoundCategory.class, in.readVarInt());
        this.x = in.readInt() / 8D;
        this.y = in.readInt() / 8D;
        this.z = in.readInt() / 8D;
        this.volume = in.readFloat();
        this.pitch = in.readFloat();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        String value = "";
        if (this.sound instanceof CustomSound) {
            value = ((CustomSound) this.sound).getName();
        } else if (this.sound instanceof BuiltinSound) {
            value = mc.minicraft.Magic.value(String.class, this.sound);
        }

        out.writeString(value);
        out.writeVarInt(mc.minicraft.Magic.value(Integer.class, this.category));
        out.writeInt((int) (this.x * 8));
        out.writeInt((int) (this.y * 8));
        out.writeInt((int) (this.z * 8));
        out.writeFloat(this.volume);
        out.writeFloat(this.pitch);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.minicraft.Util.toString(this);
    }
}
