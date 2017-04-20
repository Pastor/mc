package mc.minicraft.packet.ingame.server.level;

import mc.api.Buffer;
import mc.minicraft.ServerPlayer;
import mc.minicraft.component.level.Level;

import java.io.IOException;

public final class ServerChangeLevelPacket extends ServerStartLevelPacket {

    public int currentLevel;

    public ServerChangeLevelPacket() {
    }

    public ServerChangeLevelPacket(Level level, ServerPlayer player, int currentLevel) {
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
