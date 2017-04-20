package mc.minicraft;

import mc.api.Sound;
import mc.minicraft.data.game.ClientRequest;
import mc.minicraft.data.game.MessageType;
import mc.minicraft.data.game.entity.EntityType;
import mc.minicraft.data.game.entity.ItemType;
import mc.minicraft.data.game.entity.player.GameMode;
import mc.minicraft.data.game.setting.ChatVisibility;
import mc.minicraft.data.game.setting.Difficulty;
import mc.minicraft.data.game.world.WorldType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Magic {
    private static final Map<Object, List<Object>> values = new HashMap<>();

    static {

        {
            register(EntityType.AIR_WIZARD, 0);
            register(EntityType.ANVIL, 1);
            register(EntityType.CHEST, 2);
            register(EntityType.FURNACE, 3);
//            register(EntityType.FURNITURE, 4);
            register(EntityType.INVENTORY, 5);
            register(EntityType.ITEM_ENTITY, 6);
            register(EntityType.LANTERN, 7);
            register(EntityType.OVEN, 8);
            register(EntityType.PLAYER, 9);
            register(EntityType.SLIME, 10);
            register(EntityType.SPARK, 11);
            register(EntityType.WORKBENCH, 12);
            register(EntityType.ZOMBIE, 13);
            register(EntityType.SMASH_PARTICLE, 14);
            register(EntityType.TEXT_PARTICLE, 15);

            register(ItemType.FURNITURE, 1);
            register(ItemType.POWER_GLOVE, 2);
            register(ItemType.RESOURCE_ITEM, 3);
            register(ItemType.TOOL_ITEM, 4);

            register(Sound.Type.BOSS_DEATH, 0);
            register(Sound.Type.CRAFT, 1);
            register(Sound.Type.MONSTER_HURT, 2);
            register(Sound.Type.PICKUP, 3);
            register(Sound.Type.PLAYER_DEATH, 4);
            register(Sound.Type.PLAYER_HURT, 5);
            register(Sound.Type.TEST, 6);


            register(MinicraftProtocol.HandshakeIntent.STATUS, 1);
            register(MinicraftProtocol.HandshakeIntent.LOGIN, 2);

            register(ClientRequest.RESPAWN, 0);
            register(ClientRequest.STATS, 1);
            register(ClientRequest.OPEN_INVENTORY, 2);

            register(ChatVisibility.FULL, 0);
            register(ChatVisibility.SYSTEM, 1);
            register(ChatVisibility.HIDDEN, 2);

            register(MessageType.CHAT, 0);
            register(MessageType.SYSTEM, 1);
            register(MessageType.NOTIFICATION, 2);

            register(GameMode.SURVIVAL, 0);
            register(GameMode.CREATIVE, 1);
            register(GameMode.ADVENTURE, 2);
            register(GameMode.SPECTATOR, 3);

            register(Difficulty.PEACEFUL, 0);
            register(Difficulty.EASY, 1);
            register(Difficulty.NORMAL, 2);
            register(Difficulty.HARD, 3);

            register(WorldType.DEFAULT, "default");
            register(WorldType.FLAT, "flat");
            register(WorldType.LARGE_BIOMES, "largebiomes");
            register(WorldType.AMPLIFIED, "amplified");
            register(WorldType.CUSTOMIZED, "customized");
            register(WorldType.DEBUG, "debug_all_block_states");
            register(WorldType.DEFAULT_1_1, "default_1_1");

        }
    }

    private static void register(Enum<?> key, Object value) {
        if (!values.containsKey(key)) {
            values.put(key, new ArrayList<Object>());
        } else {
            values.get(key).removeIf(o -> value.getClass().isAssignableFrom(o.getClass()));
        }

        values.get(key).add(value);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T key(Class<T> keyType, Object value) {
        for (Object key : values.keySet()) {
            if (keyType.isAssignableFrom(key.getClass())) {
                for (Object val : values.get(key)) {
                    if (val == value || val.equals(value)) {
                        return (T) key;
                    } else if (Number.class.isAssignableFrom(val.getClass()) && Number.class.isAssignableFrom(value.getClass())) {
                        Number num = (Number) val;
                        Number num2 = (Number) value;
                        if (num.doubleValue() == num2.doubleValue()) {
                            return (T) key;
                        }
                    } else if (String.class.isAssignableFrom(val.getClass()) && String.class.isAssignableFrom(value.getClass())) {
                        String str = (String) val;
                        String str2 = (String) value;
                        if (str.equalsIgnoreCase(str2)) {
                            return (T) key;
                        }
                    }
                }
            }
        }

        throw new IllegalArgumentException("Value " + value + " has no mapping for key class " + keyType.getName() + ".");
    }

    @SuppressWarnings("unchecked")
    public static <T> T value(Class<T> valueType, Object key) {
        if (values.containsKey(key)) {
            for (Object val : values.get(key)) {
                if (valueType.isAssignableFrom(val.getClass())) {
                    return (T) val;
                } else if (Number.class.isAssignableFrom(val.getClass())) {
                    if (valueType == Byte.class) {
                        return (T) (Object) ((Number) val).byteValue();
                    } else if (valueType == Short.class) {
                        return (T) (Object) ((Number) val).shortValue();
                    } else if (valueType == Integer.class) {
                        return (T) (Object) ((Number) val).intValue();
                    } else if (valueType == Long.class) {
                        return (T) (Object) ((Number) val).longValue();
                    } else if (valueType == Float.class) {
                        return (T) (Object) ((Number) val).floatValue();
                    } else if (valueType == Double.class) {
                        return (T) (Object) ((Number) val).doubleValue();
                    }
                }
            }
        }

        throw new IllegalArgumentException("Key " + key + " has no mapping for value class " + valueType.getName() + ".");
    }
}
