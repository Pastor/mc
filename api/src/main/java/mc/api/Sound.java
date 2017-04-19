package mc.api;

public interface Sound {

    void play(int x, int y, Type type);

    enum Type {
        PLAYER_HURT,
        PLAYER_DEATH,
        MONSTER_HURT,
        TEST,
        PICKUP,
        BOSS_DEATH,
        CRAFT
    }
}
