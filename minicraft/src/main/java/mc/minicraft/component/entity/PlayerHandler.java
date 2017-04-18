package mc.minicraft.component.entity;

import mc.minicraft.component.crafting.Recipe;

import java.awt.*;
import java.util.List;

public interface PlayerHandler {

    Point move();

    boolean isAttacked();

    boolean isMenuClicked();

    boolean escapePressed();

    void won();

    void scheduleLevelChange(int dir);

    void inventoryMenu(Player player);

    void craftingMenu(Player player, List<Recipe> recipes);

    void containerMenu(Player player, String name, Inventory inventory);

    void titleMenu(Player player);
}
