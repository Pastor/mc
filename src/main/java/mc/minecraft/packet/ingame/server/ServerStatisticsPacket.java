package mc.minecraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minecraft.data.game.statistic.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerStatisticsPacket implements Packet {

    private static final String CRAFT_ITEM_PREFIX = "stats.craftItem.";
    private static final String BREAK_BLOCK_PREFIX = "stats.mineBlock.";
    private static final String USE_ITEM_PREFIX = "stats.useItem.";
    private static final String BREAK_ITEM_PREFIX = "stats.breakItem.";

    private Map<Statistic, Integer> statistics = new HashMap<Statistic, Integer>();

    @SuppressWarnings("unused")
    private ServerStatisticsPacket() {
    }

    public ServerStatisticsPacket(Map<Statistic, Integer> statistics) {
        this.statistics = statistics;
    }

    public Map<Statistic, Integer> getStatistics() {
        return this.statistics;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        int length = in.readVarInt();
        for (int index = 0; index < length; index++) {
            String value = in.readString();
            Statistic statistic = null;
            if (value.startsWith("achievement.")) {
                statistic = mc.minecraft.Magic.key(Achievement.class, value);
            } else if (value.startsWith(CRAFT_ITEM_PREFIX)) {
                statistic = new CraftItemStatistic(Integer.parseInt(value.substring(value.lastIndexOf(".") + 1)));
            } else if (value.startsWith(BREAK_BLOCK_PREFIX)) {
                statistic = new BreakBlockStatistic(Integer.parseInt(value.substring(value.lastIndexOf(".") + 1)));
            } else if (value.startsWith(USE_ITEM_PREFIX)) {
                statistic = new UseItemStatistic(Integer.parseInt(value.substring(value.lastIndexOf(".") + 1)));
            } else if (value.startsWith(BREAK_ITEM_PREFIX)) {
                statistic = new BreakItemStatistic(Integer.parseInt(value.substring(value.lastIndexOf(".") + 1)));
            } else {
                statistic = mc.minecraft.Magic.key(GenericStatistic.class, value);
            }

            this.statistics.put(statistic, in.readVarInt());
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(this.statistics.size());
        for (Statistic statistic : this.statistics.keySet()) {
            String value = "";
            if (statistic instanceof Achievement) {
                value = mc.minecraft.Magic.value(String.class, (Achievement) statistic);
            } else if (statistic instanceof CraftItemStatistic) {
                value = CRAFT_ITEM_PREFIX + ((CraftItemStatistic) statistic).getId();
            } else if (statistic instanceof BreakBlockStatistic) {
                value = BREAK_BLOCK_PREFIX + ((CraftItemStatistic) statistic).getId();
            } else if (statistic instanceof UseItemStatistic) {
                value = USE_ITEM_PREFIX + ((CraftItemStatistic) statistic).getId();
            } else if (statistic instanceof BreakItemStatistic) {
                value = BREAK_ITEM_PREFIX + ((CraftItemStatistic) statistic).getId();
            } else if (statistic instanceof GenericStatistic) {
                value = mc.minecraft.Magic.value(String.class, (GenericStatistic) statistic);
            }

            out.writeString(value);
            out.writeVarInt(this.statistics.get(statistic));
        }
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