package mc.minecraft;

import mc.api.Buffer;
import mc.engine.DefaultBuffer;
import mc.minecraft.data.game.chunk.BlockStorage;
import mc.minecraft.data.game.chunk.Chunk;
import mc.minecraft.data.game.chunk.Column;
import mc.minecraft.data.game.chunk.NibbleArray3d;
import mc.minecraft.data.game.entity.metadata.*;
import mc.minecraft.data.game.world.block.BlockFace;
import mc.minecraft.data.game.world.block.BlockState;
import mc.minecraft.data.message.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Util {
    private static String memberToString(Object o) {
        if (o == null) {
            return "null";
        }

        if (o.getClass().isArray()) {
            int length = Array.getLength(o);
            if (length > 20) {
                return o.getClass().getSimpleName() + "(length=" + length + ')';
            } else {
                StringBuilder builder = new StringBuilder("[");
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        builder.append(", ");
                    }
                    builder.append(memberToString(Array.get(o, i)));
                }
                return builder.append(']').toString();
            }
        }

        return o.toString();
    }

    public static String toString(Object o) {
        if (o == null) {
            return "null";
        }

        try {
            StringBuilder builder = new StringBuilder(o.getClass().getSimpleName()).append('(');

            // this is somewhat expensive to do every time but who cares, it's just a toString
            List<Field> allDeclaredFields = getAllDeclaredFields(o.getClass());

            for (int i = 0; i < allDeclaredFields.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                Field field = allDeclaredFields.get(i);
                field.setAccessible(true);
                builder.append(field.getName())
                        .append('=')
                        .append(memberToString(field.get(o)));
            }
            return builder.append(')').toString();
        } catch (Throwable e) {
            return o.getClass().getSimpleName() + '@' + Integer.toHexString(o.hashCode()) + '(' + e.toString() + ')';
        }
    }

    private static List<Field> getAllDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static final int POSITION_X_SIZE = 38;
    private static final int POSITION_Y_SIZE = 26;
    private static final int POSITION_Z_SIZE = 38;

    private static final int POSITION_Y_SHIFT = 0xFFF;
    private static final int POSITION_WRITE_SHIFT = 0x3FFFFFF;

    public static Object readNBT(Buffer.Input in) throws IOException {
        byte b = in.readByte();
        if (b == 0) {
            return null;
        } else {
            //return (CompoundTag) NBTIO.readTag(new NetInputStream(in, b));
            throw new UnsupportedOperationException();
        }
    }

    public static void writeNBT(Buffer.Output out, Object tag) throws IOException {
        if (tag == null) {
            out.writeByte(0);
        } else {
            //NBTIO.writeTag(new NetOutputStream(out), tag);
            throw new UnsupportedOperationException();
        }
    }

    public static BlockState readBlockState(Buffer.Input in) throws IOException {
        int rawId = in.readVarInt();
        return new BlockState(rawId >> 4, rawId & 0xF);
    }

    public static void writeBlockState(Buffer.Output out, BlockState blockState) throws IOException {
        out.writeVarInt((blockState.getId() << 4) | (blockState.getData() & 0xF));
    }

    public static ItemStack readItem(Buffer.Input in) throws IOException {
        short item = in.readShort();
        if (item < 0) {
            return null;
        } else {
            return new ItemStack(item, in.readByte(), in.readShort(), readNBT(in));
        }
    }

    public static void writeItem(Buffer.Output out, ItemStack item) throws IOException {
        if (item == null) {
            out.writeShort(-1);
        } else {
            out.writeShort(item.getId());
            out.writeByte(item.getAmount());
            out.writeShort(item.getData());
            writeNBT(out, item.getNBT());
        }
    }

    public static Position readPosition(Buffer.Input in) throws IOException {
        long val = in.readLong();

        int x = (int) (val >> POSITION_X_SIZE);
        int y = (int) ((val >> POSITION_Y_SIZE) & POSITION_Y_SHIFT);
        int z = (int) ((val << POSITION_Z_SIZE) >> POSITION_Z_SIZE);

        return new Position(x, y, z);
    }

    public static void writePosition(Buffer.Output out, Position pos) throws IOException {
        long x = pos.getX() & POSITION_WRITE_SHIFT;
        long y = pos.getY() & POSITION_Y_SHIFT;
        long z = pos.getZ() & POSITION_WRITE_SHIFT;

        out.writeLong(x << POSITION_X_SIZE | y << POSITION_Y_SIZE | z);
    }

    private static Rotation readRotation(Buffer.Input in) throws IOException {
        return new Rotation(in.readFloat(), in.readFloat(), in.readFloat());
    }

    private static void writeRotation(Buffer.Output out, Rotation rot) throws IOException {
        out.writeFloat(rot.getPitch());
        out.writeFloat(rot.getYaw());
        out.writeFloat(rot.getRoll());
    }

    public static EntityMetadata[] readEntityMetadata(Buffer.Input in) throws IOException {
        List<EntityMetadata> ret = new ArrayList<>();
        int id;
        while ((id = in.readUnsignedByte()) != 255) {
            int typeId = in.readVarInt();
            MetadataType type = Magic.key(MetadataType.class, typeId);
            Object value = null;
            switch (type) {
                case BYTE:
                    value = in.readByte();
                    break;
                case INT:
                    value = in.readVarInt();
                    break;
                case FLOAT:
                    value = in.readFloat();
                    break;
                case STRING:
                    value = in.readString();
                    break;
                case CHAT:
                    value = Message.fromString(in.readString());
                    break;
                case ITEM:
                    value = readItem(in);
                    break;
                case BOOLEAN:
                    value = in.readBoolean();
                    break;
                case ROTATION:
                    value = readRotation(in);
                    break;
                case POSITION:
                    value = readPosition(in);
                    break;
                case OPTIONAL_POSITION:
                    boolean positionPresent = in.readBoolean();
                    if (positionPresent) {
                        value = readPosition(in);
                    }

                    break;
                case BLOCK_FACE:
                    value = Magic.key(BlockFace.class, in.readVarInt());
                    break;
                case OPTIONAL_UUID:
                    boolean uuidPresent = in.readBoolean();
                    if (uuidPresent) {
                        value = in.readUUID();
                    }

                    break;
                case BLOCK_STATE:
                    value = readBlockState(in);
                    break;
                default:
                    throw new IOException("Unknown metadata type id: " + typeId);
            }

            ret.add(new EntityMetadata(id, type, value));
        }

        return ret.toArray(new EntityMetadata[ret.size()]);
    }

    public static void writeEntityMetadata(Buffer.Output out, EntityMetadata[] metadata) throws IOException {
        for (EntityMetadata meta : metadata) {
            out.writeByte(meta.getId());
            out.writeVarInt(Magic.value(Integer.class, meta.getType()));
            switch (meta.getType()) {
                case BYTE:
                    out.writeByte((Byte) meta.getValue());
                    break;
                case INT:
                    out.writeVarInt((Integer) meta.getValue());
                    break;
                case FLOAT:
                    out.writeFloat((Float) meta.getValue());
                    break;
                case STRING:
                    out.writeString((String) meta.getValue());
                    break;
                case CHAT:
                    out.writeString(((Message) meta.getValue()).toJsonString());
                    break;
                case ITEM:
                    writeItem(out, (ItemStack) meta.getValue());
                    break;
                case BOOLEAN:
                    out.writeBoolean((Boolean) meta.getValue());
                    break;
                case ROTATION:
                    writeRotation(out, (Rotation) meta.getValue());
                    break;
                case POSITION:
                    writePosition(out, (Position) meta.getValue());
                    break;
                case OPTIONAL_POSITION:
                    out.writeBoolean(meta.getValue() != null);
                    if (meta.getValue() != null) {
                        writePosition(out, (Position) meta.getValue());
                    }

                    break;
                case BLOCK_FACE:
                    out.writeVarInt(Magic.value(Integer.class, meta.getValue()));
                    break;
                case OPTIONAL_UUID:
                    out.writeBoolean(meta.getValue() != null);
                    if (meta.getValue() != null) {
                        out.writeUUID((UUID) meta.getValue());
                    }

                    break;
                case BLOCK_STATE:
                    writeBlockState(out, (BlockState) meta.getValue());
                    break;
                default:
                    throw new IOException("Unknown metadata type: " + meta.getType());
            }
        }

        out.writeByte(255);
    }

    public static Column readColumn(byte data[], int x, int z, boolean fullChunk, boolean hasSkylight, int mask, Object[] tileEntities) throws IOException {
        Buffer.Input in = DefaultBuffer.instance().newInput(new ByteArrayInputStream(data));
        Exception ex = null;
        Column column = null;
        try {
            Chunk[] chunks = new Chunk[16];
            for (int index = 0; index < chunks.length; index++) {
                if ((mask & (1 << index)) != 0) {
                    BlockStorage blocks = new BlockStorage(in);
                    NibbleArray3d blocklight = new NibbleArray3d(in, 2048);
                    NibbleArray3d skylight = hasSkylight ? new NibbleArray3d(in, 2048) : null;
                    chunks[index] = new Chunk(blocks, blocklight, skylight);
                }
            }

            byte biomeData[] = null;
            if (fullChunk) {
                biomeData = in.readBytes(256);
            }

            column = new Column(x, z, chunks, biomeData, tileEntities);
        } catch (Exception e) {
            ex = e;
        }

        // Unfortunately, this is needed to detect whether the chunks contain skylight or not.
        if ((in.available() > 0 || ex != null) && !hasSkylight) {
            return readColumn(data, x, z, fullChunk, true, mask, tileEntities);
        } else if (ex != null) {
            throw new IOException("Failed to read chunk data.", ex);
        }

        return column;
    }

    public static int writeColumn(Buffer.Output out, Column column, boolean fullChunk, boolean hasSkylight) throws IOException {
        int mask = 0;
        Chunk chunks[] = column.getChunks();
        for (int index = 0; index < chunks.length; index++) {
            Chunk chunk = chunks[index];
            if (chunk != null && (!fullChunk || !chunk.isEmpty())) {
                mask |= 1 << index;
                chunk.getBlocks().write(out);
                chunk.getBlockLight().write(out);
                if (hasSkylight) {
                    chunk.getSkyLight().write(out);
                }
            }
        }

        if (fullChunk) {
            out.writeBytes(column.getBiomeData());
        }

        return mask;
    }
}
