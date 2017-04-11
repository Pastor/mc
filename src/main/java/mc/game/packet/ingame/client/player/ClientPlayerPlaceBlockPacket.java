package mc.game.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.metadata.Position;
import mc.game.data.game.entity.player.Hand;
import mc.game.data.game.world.block.BlockFace;

import java.io.IOException;

public class ClientPlayerPlaceBlockPacket implements Packet {

    private Position position;
    private BlockFace face;
    private Hand hand;
    private float cursorX;
    private float cursorY;
    private float cursorZ;

    @SuppressWarnings("unused")
    private ClientPlayerPlaceBlockPacket() {
    }

    public ClientPlayerPlaceBlockPacket(Position position, BlockFace face, Hand hand, float cursorX, float cursorY, float cursorZ) {
        this.position = position;
        this.face = face;
        this.hand = hand;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
        this.cursorZ = cursorZ;
    }

    public Position getPosition() {
        return this.position;
    }

    public BlockFace getFace() {
        return this.face;
    }

    public Hand getHand() {
        return this.hand;
    }

    public float getCursorX() {
        return this.cursorX;
    }

    public float getCursorY() {
        return this.cursorY;
    }

    public float getCursorZ() {
        return this.cursorZ;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.position = mc.game.Util.readPosition(in);
        this.face = mc.game.Magic.key(BlockFace.class, in.readVarInt());
        this.hand = mc.game.Magic.key(Hand.class, in.readVarInt());
        this.cursorX = in.readFloat();
        this.cursorY = in.readFloat();
        this.cursorZ = in.readFloat();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        mc.game.Util.writePosition(out, this.position);
        out.writeVarInt(mc.game.Magic.value(Integer.class, this.face));
        out.writeVarInt(mc.game.Magic.value(Integer.class, this.hand));
        out.writeFloat(this.cursorX);
        out.writeFloat(this.cursorY);
        out.writeFloat(this.cursorZ);
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
