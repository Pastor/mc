package mc.minicraft.engine.item.resource;

import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.level.BaseLevel;
import mc.minicraft.engine.level.tile.Tile;

public final class FoodResource extends Resource {
    private int heal;
    private int staminaCost;

    public FoodResource(String name, int sprite, int color, int heal, int staminaCost) {
        super(name, sprite, color);
        this.heal = heal;
        this.staminaCost = staminaCost;
    }

    public boolean interactOn(Tile tile, BaseLevel level, int xt, int yt, Player player, int attackDir) {
        if (player.health < player.maxHealth && player.payStamina(staminaCost)) {
            player.heal(heal);
            return true;
        }
        return false;
    }
}
