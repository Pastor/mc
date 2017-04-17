package mc.minecraft.client;

import mc.minecraft.client.screen.ContainerMenu;
import mc.minecraft.client.screen.CraftingMenu;
import mc.minecraft.client.screen.InventoryMenu;
import mc.minicraft.component.crafting.Recipe;
import mc.minicraft.component.entity.Inventory;
import mc.minicraft.component.entity.Player;
import mc.minicraft.component.entity.PlayerHandler;

import java.util.List;

public final class ClientPlayerHandler implements PlayerHandler {
    private final InputHandler input;
    private final Game game;

    public ClientPlayerHandler(InputHandler input, Game game) {
        this.input = input;
        this.game = game;
    }

    @Override
    public boolean isAttacked() {
        return input.attack.clicked;
    }

    @Override
    public boolean isMenuClicked() {
        return input.menu.clicked;
    }

    @Override
    public boolean upPressed() {
        return input.up.down;
    }

    @Override
    public boolean downPressed() {
        return input.down.down;
    }

    @Override
    public boolean leftPressed() {
        return input.left.down;
    }

    @Override
    public boolean rightPressed() {
        return input.right.down;
    }

    @Override
    public boolean escapePressed() {
        return input.escape.clicked;
    }

    @Override
    public void won() {
        game.won();
    }

    @Override
    public void scheduleLevelChange(int dir) {
        game.scheduleLevelChange(dir);
    }

    @Override
    public void inventoryMenu(Player player) {
        game.setMenu(new InventoryMenu(player));
    }

    @Override
    public void craftingMenu(Player player, List<Recipe> recipes) {
        game.setMenu(new CraftingMenu(recipes, player));
    }

    @Override
    public void containerMenu(Player player, String name, Inventory inventory) {
        game.setMenu(new ContainerMenu(player, name, inventory));
    }

    @Override
    public void titleMenu(Player player) {
        game.titleMenu();
    }
}
