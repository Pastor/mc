package mc.game.data.status;

import mc.game.Profile;

import java.util.Arrays;

public class PlayerInfo {
    private int max;
    private int online;
    private Profile players[];

    public PlayerInfo(int max, int online, Profile players[]) {
        this.max = max;
        this.online = online;
        this.players = players;
    }

    public int getMaxPlayers() {
        return this.max;
    }

    public int getOnlinePlayers() {
        return this.online;
    }

    public Profile[] getPlayers() {
        return this.players;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PlayerInfo && this.max == ((PlayerInfo) o).max && this.online == ((PlayerInfo) o).online && Arrays.deepEquals(this.players, ((PlayerInfo) o).players);
    }

    @Override
    public int hashCode() {
        int result = this.max;
        result = 31 * result + this.online;
        result = 31 * result + Arrays.deepHashCode(this.players);
        return result;
    }
}
