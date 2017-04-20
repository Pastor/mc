package mc.minicraft.packet.ingame.server.level;

import mc.minicraft.ServerPlayer;
import mc.minicraft.engine.level.ServerLevel;

public final class ServerStartLevelPacket extends ServerLevelPositionPacket {
    @SuppressWarnings({"unused", "WeakerAccess"})
    public ServerStartLevelPacket() {
    }

    public ServerStartLevelPacket(ServerLevel level, ServerPlayer player) {
        super(level, player);
    }
}
