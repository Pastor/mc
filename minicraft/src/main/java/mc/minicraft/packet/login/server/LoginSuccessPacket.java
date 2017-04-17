package mc.minicraft.packet.login.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.Profile;
import mc.minicraft.Util;

import java.io.IOException;

public class LoginSuccessPacket implements Packet {

    private Profile profile;

    @SuppressWarnings("unused")
    private LoginSuccessPacket() {
    }

    public LoginSuccessPacket(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return this.profile;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.profile = new Profile(in.readString(), in.readString());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.profile.id);
        out.writeString(this.profile.name);
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
