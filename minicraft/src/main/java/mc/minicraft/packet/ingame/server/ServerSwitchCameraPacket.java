package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerSwitchCameraPacket implements Packet {

    private int cameraEntityId;

    @SuppressWarnings("unused")
    private ServerSwitchCameraPacket() {
    }

    public ServerSwitchCameraPacket(int cameraEntityId) {
        this.cameraEntityId = cameraEntityId;
    }

    public int getCameraEntityId() {
        return this.cameraEntityId;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.cameraEntityId = in.readVarInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.cameraEntityId);
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
