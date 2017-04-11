package mc.minecraft.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.metadata.Position;

import java.io.IOException;

public class ServerSpawnPositionPacket implements Packet {

    private Position position;

    @SuppressWarnings("unused")
    private ServerSpawnPositionPacket() {
    }

    public ServerSpawnPositionPacket(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.position = mc.minecraft.Util.readPosition(in);
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        mc.minecraft.Util.writePosition(out, this.position);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.minecraft.Util.toString(this);
    }
}
