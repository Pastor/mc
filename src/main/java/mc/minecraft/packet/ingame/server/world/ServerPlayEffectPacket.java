package mc.minecraft.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.metadata.Position;
import mc.minecraft.data.game.world.block.BlockState;
import mc.minecraft.data.game.world.effect.*;

import java.io.IOException;

public class ServerPlayEffectPacket implements Packet {

    private WorldEffect effect;
    private Position position;
    private WorldEffectData data;
    private boolean broadcast;

    @SuppressWarnings("unused")
    private ServerPlayEffectPacket() {
    }

    public ServerPlayEffectPacket(WorldEffect effect, Position position, WorldEffectData data) {
        this(effect, position, data, false);
    }

    public ServerPlayEffectPacket(WorldEffect effect, Position position, WorldEffectData data, boolean broadcast) {
        this.effect = effect;
        this.position = position;
        this.data = data;
        this.broadcast = broadcast;
    }

    public WorldEffect getEffect() {
        return this.effect;
    }

    public Position getPosition() {
        return this.position;
    }

    public WorldEffectData getData() {
        return this.data;
    }

    public boolean getBroadcast() {
        return this.broadcast;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.effect = mc.minecraft.Magic.key(WorldEffect.class, in.readInt());
        this.position = mc.minecraft.Util.readPosition(in);
        int value = in.readInt();
        if (this.effect == SoundEffect.RECORD) {
            this.data = new RecordEffectData(value);
        } else if (this.effect == ParticleEffect.SMOKE) {
            this.data = mc.minecraft.Magic.key(SmokeEffectData.class, value);
        } else if (this.effect == ParticleEffect.BREAK_BLOCK) {
            this.data = new BreakBlockEffectData(new BlockState(value & 4095, (value >> 12) & 255));
        } else if (this.effect == ParticleEffect.BREAK_SPLASH_POTION) {
            this.data = new BreakPotionEffectData(value);
        } else if (this.effect == ParticleEffect.BONEMEAL_GROW) {
            this.data = new BonemealGrowEffectData(value);
        }

        this.broadcast = in.readBoolean();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeInt(mc.minecraft.Magic.value(Integer.class, this.effect));
        mc.minecraft.Util.writePosition(out, this.position);
        int value = 0;
        if (this.data instanceof RecordEffectData) {
            value = ((RecordEffectData) this.data).getRecordId();
        } else if (this.data instanceof SmokeEffectData) {
            value = mc.minecraft.Magic.value(Integer.class, (SmokeEffectData) this.data);
        } else if (this.data instanceof BreakBlockEffectData) {
            value = (((BreakBlockEffectData) this.data).getBlockState().getId() & 4095) | ((((BreakBlockEffectData) this.data).getBlockState().getData() & 255) << 12);
        } else if (this.data instanceof BreakPotionEffectData) {
            value = ((BreakPotionEffectData) this.data).getPotionId();
        } else if (this.data instanceof BonemealGrowEffectData) {
            value = ((BonemealGrowEffectData) this.data).getParticleCount();
        }

        out.writeInt(value);
        out.writeBoolean(this.broadcast);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.minecraft.Util.toString(this);
    }
}
