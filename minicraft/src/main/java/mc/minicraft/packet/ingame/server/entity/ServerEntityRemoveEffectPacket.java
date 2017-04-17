package mc.minicraft.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.entity.Effect;

import java.io.IOException;

public class ServerEntityRemoveEffectPacket implements Packet {

    private int entityId;
    private Effect effect;

    @SuppressWarnings("unused")
    private ServerEntityRemoveEffectPacket() {
    }

    public ServerEntityRemoveEffectPacket(int entityId, Effect effect) {
        this.entityId = entityId;
        this.effect = effect;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Effect getEffect() {
        return this.effect;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.effect = mc.minicraft.Magic.key(Effect.class, in.readUnsignedByte());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeByte(mc.minicraft.Magic.value(Integer.class, this.effect));
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
