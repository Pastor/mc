package mc.minicraft.component.sound;

public interface Sound {

    void play(Type type);

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
