package mc.minicraft.component.entity;

import mc.minicraft.component.crafting.Recipe;

import java.awt.*;
import java.util.List;

public final class PlayerHandlerAdapter implements PlayerHandler {


    @Override
    public Point move() {
        int x = 0;
        int y = 0;
        return new Point(x, y);
    }

    @Override
    public boolean isAttacked() {
        return false;
    }

    @Override
    public boolean isMenuClicked() {
        return false;
    }

    @Override
    public boolean escapePressed() {
        return false;
    }

    @Override
    public void won() {

    }

    @Override
    public void scheduleLevelChange(int dir) {

    }

    @Override
    public void inventoryMenu(Player player) {

    }

    @Override
    public void craftingMenu(Player player, List<Recipe> recipes) {

    }

    @Override
    public void containerMenu(Player player, String name, Inventory inventory) {

    }

    @Override
    public void mainMenu(Player player) {

    }
}
