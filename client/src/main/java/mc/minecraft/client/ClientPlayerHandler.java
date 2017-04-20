package mc.minecraft.client;

import mc.api.Client;
import mc.minecraft.client.screen.ContainerMenu;
import mc.minecraft.client.screen.CraftingMenu;
import mc.minecraft.client.screen.InventoryMenu;
import mc.minicraft.engine.crafting.Recipe;
import mc.minicraft.engine.entity.Inventory;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.entity.PlayerHandler;

import java.awt.*;
import java.util.List;

public final class ClientPlayerHandler implements PlayerHandler {
    private final InputHandler input;
    private final Client client;
    private final Game game;

    public ClientPlayerHandler(InputHandler input, Client client, Game game) {
        this.input = input;
        this.client = client;
        this.game = game;
    }

    @Override
    public Point move() {
        int xa = 0;
        int ya = 0;
        if (input.up.down)
            ya--;
        if (input.down.down)
            ya++;
        if (input.left.down)
            xa--;
        if (input.right.down)
            xa++;
        return new Point(xa, ya);
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
    public boolean escapePressed() {
        return input.escape.clicked;
    }

    @Override
    public void won() {
        game.won();
    }

    @Override
    public void scheduleLevelChange(int dir) {
    }

    @Override
    public void inventoryMenu(Player player) {
        game.setMenu(new InventoryMenu(client, player));
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
    public void mainMenu(Player player) {
        game.mainMenu();
    }
}
