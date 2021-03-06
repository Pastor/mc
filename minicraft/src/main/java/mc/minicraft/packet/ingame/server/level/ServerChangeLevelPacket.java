package mc.minicraft.packet.ingame.server.level;

import mc.api.Buffer;
import mc.minicraft.ServerPlayer;
import mc.minicraft.engine.level.ServerLevel;

import java.io.IOException;

public final class ServerChangeLevelPacket extends ServerLevelPositionPacket {

    public int currentLevel;

    @SuppressWarnings({"unused", "WeakerAccess"})
    public ServerChangeLevelPacket() {
    }

    public ServerChangeLevelPacket(ServerLevel level, ServerPlayer player, int currentLevel) {
        super(level, player);
        this.currentLevel = currentLevel;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        super.read(in);
        currentLevel = in.readVarInt();
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        super.write(out);
        out.writeVarInt(currentLevel);
    }
}
