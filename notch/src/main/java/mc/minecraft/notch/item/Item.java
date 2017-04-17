package mc.minecraft.notch.item;

import mc.minecraft.notch.entity.Entity;
import mc.minecraft.notch.entity.ItemEntity;
import mc.minecraft.notch.entity.Player;
import mc.minecraft.notch.gfx.Screen;
import mc.minecraft.notch.level.Level;
import mc.minecraft.notch.level.tile.Tile;
import mc.minecraft.notch.screen.ListItem;

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