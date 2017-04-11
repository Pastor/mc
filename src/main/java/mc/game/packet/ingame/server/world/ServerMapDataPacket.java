package mc.game.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.world.map.MapData;
import mc.game.data.game.world.map.MapIcon;
import mc.game.data.game.world.map.MapIconType;

import java.io.IOException;

public class ServerMapDataPacket implements Packet {

    private int mapId;
    private byte scale;
    private boolean trackingPosition;
    private MapIcon icons[];

    private MapData data;

    @SuppressWarnings("unused")
    private ServerMapDataPacket() {
    }

    public ServerMapDataPacket(int mapId, byte scale, boolean trackingPosition, MapIcon icons[]) {
        this(mapId, scale, trackingPosition, icons, null);
    }

    public ServerMapDataPacket(int mapId, byte scale, boolean trackingPosition, MapIcon icons[], MapData data) {
        this.mapId = mapId;
        this.scale = scale;
        this.trackingPosition = trackingPosition;
        this.icons = icons;
        this.data = data;
    }

    public int getMapId() {
        return this.mapId;
    }

    public byte getScale() {
        return this.scale;
    }

    public boolean getTrackingPosition() {
        return this.trackingPosition;
    }

    public MapIcon[] getIcons() {
        return this.icons;
    }

    public MapData getData() {
        return this.data;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.mapId = in.readVarInt();
        this.scale = in.readByte();
        this.trackingPosition = in.readBoolean();
        this.icons = new MapIcon[in.readVarInt()];
        for (int index = 0; index < this.icons.length; index++) {
            int data = in.readUnsignedByte();
            int type = (data >> 4) & 15;
            int rotation = data & 15;
            int x = in.readUnsignedByte();
            int z = in.readUnsignedByte();
            this.icons[index] = new MapIcon(x, z, mc.game.Magic.value(MapIconType.class, type), rotation);
        }

        int columns = in.readUnsignedByte();
        if (columns > 0) {
            int rows = in.readUnsignedByte();
            int x = in.readUnsignedByte();
            int y = in.readUnsignedByte();
            byte data[] = in.readBytes(in.readVarInt());
            this.data = new MapData(columns, rows, x, y, data);
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.mapId);
        out.writeByte(this.scale);
        out.writeBoolean(this.trackingPosition);
        out.writeVarInt(this.icons.length);
        for (int index = 0; index < this.icons.length; index++) {
            MapIcon icon = this.icons[index];
            int type = mc.game.Magic.key(Integer.class, icon.getIconType());
            out.writeByte((type & 15) << 4 | icon.getIconRotation() & 15);
            out.writeByte(icon.getCenterX());
            out.writeByte(icon.getCenterZ());
        }

        if (this.data != null && this.data.getColumns() != 0) {
            out.writeByte(this.data.getColumns());
            out.writeByte(this.data.getRows());
            out.writeByte(this.data.getX());
            out.writeByte(this.data.getY());
            out.writeVarInt(this.data.getData().length);
            out.writeBytes(this.data.getData());
        } else {
            out.writeByte(0);
        }
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.game.Util.toString(this);
    }
}
