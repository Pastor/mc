package mc.minicraft.packet.ingame.client;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.Magic;
import mc.minicraft.data.game.ClientRequest;

import java.io.IOException;

public class ClientRequestPacket implements Packet {

    private ClientRequest request;

    @SuppressWarnings("unused")
    private ClientRequestPacket() {
    }

    public ClientRequestPacket(ClientRequest request) {
        this.request = request;
    }

    public ClientRequest getRequest() {
        return this.request;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.request = Magic.key(ClientRequest.class, in.readVarInt());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(Magic.value(Integer.class, this.request));
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
