package mc.minicraft.packet.ingame.client.player;

import mc.api.Buffer;
import mc.api.Packet;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.engine.entity.Entity;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.entity.PlayerHandler;

import java.io.IOException;

public final class ClientPlayerUpdatePacket implements Packet {

    public Sound sound;
    public PlayerHandler handler;
    public PropertyReader reader;

    public Player player;

    @Override
    public void read(Buffer.Input in) throws IOException {
        player = (Player) Entity.readEntity(sound, handler, reader, in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        player.write(out);
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
