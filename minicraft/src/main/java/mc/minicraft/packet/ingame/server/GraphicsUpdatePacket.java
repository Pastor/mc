package mc.minicraft.packet.ingame.server;

import mc.api.Packet;
import mc.api.Sound;
import mc.engine.property.PropertyReader;
import mc.minicraft.engine.entity.PlayerHandler;

public abstract class GraphicsUpdatePacket implements Packet {
    public Sound sound;
    public PlayerHandler handler;
    public PropertyReader property;
}
