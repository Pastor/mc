package mc.minicraft.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.entity.player.Hand;

import java.io.IOException;

public class ClientPlayerUseItemPacket implements Packet {
    private Hand hand;

    @SuppressWarnings("unused")
    private ClientPlayerUseItemPacket() {
    }

    public ClientPlayerUseItemPacket(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return this.hand;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.hand = mc.minicraft.Magic.key(Hand.class, in.readVarInt());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(mc.minicraft.Magic.value(Integer.class, this.hand));
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
