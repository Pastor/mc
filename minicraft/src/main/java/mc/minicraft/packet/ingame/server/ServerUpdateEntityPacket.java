package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.minicraft.engine.entity.Entity;

import java.io.IOException;

public final class ServerUpdateEntityPacket extends GraphicsUpdatePacket {

    public Entity entity;

    @Override
    public void read(Buffer.Input in) throws IOException {
        entity = Entity.readEntity(sound, handler, property, in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        entity.write(out);
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
