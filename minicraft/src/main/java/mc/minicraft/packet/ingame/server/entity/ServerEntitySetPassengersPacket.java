package mc.minicraft.packet.ingame.server.entity;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public class ServerEntitySetPassengersPacket implements Packet {

    private int entityId;
    private int passengerIds[];

    @SuppressWarnings("unused")
    private ServerEntitySetPassengersPacket() {
    }

    public ServerEntitySetPassengersPacket(int entityId, int... passengerIds) {
        this.entityId = entityId;
        this.passengerIds = passengerIds;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int[] getPassengerIds() {
        return this.passengerIds;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.entityId = in.readVarInt();
        this.passengerIds = new int[in.readVarInt()];
        for (int index = 0; index < this.passengerIds.length; index++) {
            this.passengerIds[index] = in.readVarInt();
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeVarInt(this.passengerIds.length);
        for (int entityId : this.passengerIds) {
            out.writeVarInt(entityId);
        }
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.minicraft.Util.toString(this);
    }
}
