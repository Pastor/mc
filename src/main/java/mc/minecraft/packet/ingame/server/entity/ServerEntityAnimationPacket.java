package mc.minecraft.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.player.Animation;

import java.io.IOException;

public class ServerEntityAnimationPacket implements Packet {

    private int entityId;
    private Animation animation;

    @SuppressWarnings("unused")
    private ServerEntityAnimationPacket() {
    }

    public ServerEntityAnimationPacket(int entityId, Animation animation) {
        this.entityId = entityId;
        this.animation = animation;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.animation = mc.minecraft.Magic.key(Animation.class, in.readUnsignedByte());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeByte(mc.minecraft.Magic.value(Integer.class, this.animation));
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