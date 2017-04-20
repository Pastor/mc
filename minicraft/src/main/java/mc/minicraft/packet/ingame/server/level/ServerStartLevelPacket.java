package mc.minicraft.packet.ingame.server.level;

import mc.minicraft.ServerPlayer;
import mc.minicraft.component.level.Level;

public final class ServerStartLevelPacket extends ServerLevelPositionPacket {
    @SuppressWarnings({"unused", "WeakerAccess"})
    public ServerStartLevelPacket() {
    }

    public ServerStartLevelPacket(Level level, ServerPlayer player) {
        super(level, player);
    }
}
