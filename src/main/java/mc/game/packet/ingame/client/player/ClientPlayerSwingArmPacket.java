package mc.game.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.player.Hand;

import java.io.IOException;

public class ClientPlayerSwingArmPacket implements Packet {
    private Hand hand;

    @SuppressWarnings("unused")
    private ClientPlayerSwingArmPacket() {
    }

    public ClientPlayerSwingArmPacket(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return this.hand;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.hand = mc.game.Magic.key(Hand.class, in.readVarInt());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(mc.game.Magic.value(Integer.class, this.hand));
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
