package mc.minicraft.packet.login.client;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.Util;

import java.io.IOException;

public class LoginStartPacket implements Packet {

    private String username;

    @SuppressWarnings("unused")
    private LoginStartPacket() {
    }

    public LoginStartPacket(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.username = in.readString();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.username);
    }

    @Override
    public boolean isPriority() {
        return true;
    }

    @Override
    public String toString() {
        return Util.toString(this);
    }
}
