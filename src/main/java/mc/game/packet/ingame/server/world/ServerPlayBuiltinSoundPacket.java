package mc.game.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.world.sound.BuiltinSound;
import mc.game.data.game.world.sound.SoundCategory;

import java.io.IOException;

public class ServerPlayBuiltinSoundPacket implements Packet {

    private BuiltinSound sound;
    private SoundCategory category;
    private double x;
    private double y;
    private double z;
    private float volume;
    private float pitch;

    @SuppressWarnings("unused")
    private ServerPlayBuiltinSoundPacket() {
    }

    public ServerPlayBuiltinSoundPacket(BuiltinSound sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {
        this.sound = sound;
        this.category = category;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    public BuiltinSound getSound() {
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
        this.sound = mc.game.Magic.key(BuiltinSound.class, in.readVarInt());
        this.category = mc.game.Magic.key(SoundCategory.class, in.readVarInt());
        this.x = in.readInt() / 8D;
        this.y = in.readInt() / 8D;
        this.z = in.readInt() / 8D;
        this.volume = in.readFloat();
        this.pitch = in.readFloat();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(mc.game.Magic.value(Integer.class, this.sound));
        out.writeVarInt(mc.game.Magic.value(Integer.class, this.category));
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
        return mc.game.Util.toString(this);
    }
}
