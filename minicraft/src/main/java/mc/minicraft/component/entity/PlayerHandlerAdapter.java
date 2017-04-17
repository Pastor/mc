package mc.minicraft.component.entity;

import mc.minicraft.component.crafting.Recipe;

import java.util.List;

public class PlayerHandlerAdapter implements PlayerHandler {
    @Override
    public boolean isAttacked() {
        return false;
    }

    @Override
    public boolean isMenuClicked() {
        return false;
    }

    @Override
    public boolean upPressed() {
        return false;
    }

    @Override
    public boolean downPressed() {
        return false;
    }

    @Override
    public boolean leftPressed() {
        return false;
    }

    @Override
    public boolean rightPressed() {
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
    public void titleMenu(Player player) {

    }
}
