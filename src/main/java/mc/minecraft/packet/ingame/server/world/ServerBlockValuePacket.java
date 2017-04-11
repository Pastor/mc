package mc.minecraft.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.entity.metadata.Position;
import mc.minecraft.data.game.world.block.value.*;

import java.io.IOException;

public class ServerBlockValuePacket implements Packet {

    private static final int NOTE_BLOCK = 25;
    private static final int STICKY_PISTON = 29;
    private static final int PISTON = 33;
    private static final int MOB_SPAWNER = 52;
    private static final int CHEST = 54;
    private static final int ENDER_CHEST = 130;
    private static final int TRAPPED_CHEST = 146;
    private static final int SHULKER_BOX_LOWER = 219;
    private static final int SHULKER_BOX_HIGHER = 234;

    private Position position;
    private BlockValueType type;
    private BlockValue value;
    private int blockId;

    @SuppressWarnings("unused")
    private ServerBlockValuePacket() {
    }

    public ServerBlockValuePacket(Position position, BlockValueType type, BlockValue value, int blockId) {
        this.position = position;
        this.type = type;
        this.value = value;
        this.blockId = blockId;
    }

    public Position getPosition() {
        return this.position;
    }

    public BlockValueType getType() {
        return this.type;
    }

    public BlockValue getValue() {
        return this.value;
    }

    public int getBlockId() {
        return this.blockId;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.position = mc.minecraft.Util.readPosition(in);
        int type = in.readUnsignedByte();
        int value = in.readUnsignedByte();
        this.blockId = in.readVarInt() & 0xFFF;

        if (this.blockId == NOTE_BLOCK) {
            this.type = mc.minecraft.Magic.key(NoteBlockValueType.class, type);
            this.value = new NoteBlockValue(value);
        } else if (this.blockId == STICKY_PISTON || this.blockId == PISTON) {
            this.type = mc.minecraft.Magic.key(PistonValueType.class, type);
            this.value = mc.minecraft.Magic.key(PistonValue.class, value);
        } else if (this.blockId == MOB_SPAWNER) {
            this.type = mc.minecraft.Magic.key(MobSpawnerValueType.class, type);
            this.value = new MobSpawnerValue();
        } else if (this.blockId == CHEST || this.blockId == ENDER_CHEST || this.blockId == TRAPPED_CHEST
                || (this.blockId >= SHULKER_BOX_LOWER && this.blockId <= SHULKER_BOX_HIGHER)) {
            this.type = mc.minecraft.Magic.key(ChestValueType.class, type);
            this.value = new ChestValue(value);
        } else {
            this.type = mc.minecraft.Magic.key(GenericBlockValueType.class, type);
            this.value = new GenericBlockValue(value);
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        mc.minecraft.Util.writePosition(out, this.position);
        int type = 0;
        if (this.type instanceof NoteBlockValueType) {
            type = mc.minecraft.Magic.value(Integer.class, (NoteBlockValueType) this.type);
        } else if (this.type instanceof PistonValueType) {
            type = mc.minecraft.Magic.value(Integer.class, (PistonValueType) this.type);
        } else if (this.type instanceof MobSpawnerValueType) {
            type = mc.minecraft.Magic.value(Integer.class, (MobSpawnerValueType) this.type);
        } else if (this.type instanceof ChestValueType) {
            type = mc.minecraft.Magic.value(Integer.class, (ChestValueType) this.type);
        } else if (this.type instanceof GenericBlockValueType) {
            type = mc.minecraft.Magic.value(Integer.class, (GenericBlockValueType) this.type);
        }

        out.writeByte(type);
        int val = 0;
        if (this.value instanceof NoteBlockValue) {
            val = ((NoteBlockValue) this.value).getPitch();
        } else if (this.value instanceof PistonValue) {
            val = mc.minecraft.Magic.value(Integer.class, (PistonValue) this.value);
        } else if (this.value instanceof MobSpawnerValue) {
            val = 0;
        } else if (this.value instanceof ChestValue) {
            val = ((ChestValue) this.value).getViewers();
        } else if (this.value instanceof GenericBlockValue) {
            val = ((GenericBlockValue) this.value).getValue();
        }

        out.writeByte(val);
        out.writeVarInt(this.blockId & 4095);
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
