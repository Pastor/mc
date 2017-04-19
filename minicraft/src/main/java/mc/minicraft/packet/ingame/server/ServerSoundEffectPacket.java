package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.api.Sound;
import mc.minicraft.Magic;

import java.io.IOException;

public final class ServerSoundEffectPacket implements Packet {

    public Sound.Type type = Sound.Type.TEST;

    @Override
    public void read(Buffer.Input in) throws IOException {
        type = Magic.key(Sound.Type.class, in.readByte());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(Magic.value(Integer.class, type));
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
