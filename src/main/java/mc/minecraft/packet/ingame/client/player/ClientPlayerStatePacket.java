package mc.minecraft.packet.ingame.client.player;


import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.player.PlayerState;

import java.io.IOException;

public class ClientPlayerStatePacket implements Packet {

    private int entityId;
    private PlayerState state;
    private int jumpBoost;

    @SuppressWarnings("unused")
    private ClientPlayerStatePacket() {
    }

    public ClientPlayerStatePacket(int entityId, PlayerState state) {
        this(entityId, state, 0);
    }

    public ClientPlayerStatePacket(int entityId, PlayerState state, int jumpBoost) {
        this.entityId = entityId;
        this.state = state;
        this.jumpBoost = jumpBoost;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public PlayerState getState() {
        return this.state;
    }

    public int getJumpBoost() {
        return this.jumpBoost;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.state = mc.minecraft.Magic.key(PlayerState.class, in.readVarInt());
        this.jumpBoost = in.readVarInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeVarInt(mc.minecraft.Magic.value(Integer.class, this.state));
        out.writeVarInt(this.jumpBoost);
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
