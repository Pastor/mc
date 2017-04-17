package mc.minicraft.packet.ingame.client.window;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ClientConfirmTransactionPacket implements Packet {

    private int windowId;
    private int actionId;
    private boolean accepted;

    @SuppressWarnings("unused")
    private ClientConfirmTransactionPacket() {
    }

    public ClientConfirmTransactionPacket(int windowId, int actionId, boolean accepted) {
        this.windowId = windowId;
        this.actionId = actionId;
        this.accepted = accepted;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getActionId() {
        return this.actionId;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.windowId = in.readByte();
        this.actionId = in.readShort();
        this.accepted = in.readBoolean();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(this.windowId);
        out.writeShort(this.actionId);
        out.writeBoolean(this.accepted);
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
