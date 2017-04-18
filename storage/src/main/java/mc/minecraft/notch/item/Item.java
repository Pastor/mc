package mc.minecraft.client.item;

import mc.minicraft.component.entity.Entity;
import mc.minicraft.component.entity.ItemEntity;
import mc.minicraft.component.entity.Player;
import mc.minicraft.component.gfx.Screen;
import mc.minecraft.client.level.Level;
import mc.minecraft.client.level.tile.Tile;
import mc.minecraft.client.screen.ListItem;

public class Item implements ListItem {
    public int getColor() {
        return 0;
    }

    public int getSprite() {
        return 0;
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public void renderInventory(Screen screen, int x, int y) {
    }

    public boolean interact(Player player, Entity entity, int attackDir) {
        return false;
    }

    public void renderIcon(Screen screen, int x, int y) {
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        return false;
    }

    public boolean isDepleted() {
        return false;
    }

    public boolean canAttack() {
        return false;
    }

    public int getAttackDamageBonus(Entity e) {
        return 0;
    }

    public String getName() {
        return "";
    }

    public boolean matches(Item item) {
        return item.getClass() == getClass();
    }
}