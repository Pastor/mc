package mc.game.packet.ingame.client.world;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientSteerBoatPacket implements Packet {

    private boolean unk1;
    private boolean unk2;

    @SuppressWarnings("unused")
    private ClientSteerBoatPacket() {
    }

    public ClientSteerBoatPacket(boolean unk1, boolean unk2) {
        this.unk1 = unk1;
        this.unk2 = unk2;
    }

    public boolean getUnknown1() {
        return this.unk1;
    }

    public boolean getUnknown2() {
        return this.unk2;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.unk1 = in.readBoolean();
        this.unk2 = in.readBoolean();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeBoolean(this.unk1);
        out.writeBoolean(this.unk2);
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
