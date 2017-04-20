package mc.minecraft.client.screen;

import mc.api.Client;
import mc.minicraft.engine.entity.Player;
import mc.minecraft.client.gfx.Font;
import mc.minecraft.client.gfx.ClientScreen;
import mc.minicraft.engine.item.Item;
import mc.minicraft.packet.ingame.client.player.ClientPlayerUpdatePacket;

public final class InventoryMenu extends Menu {
    private final Client client;
    private final Player player;
    private int selected = 0;

    public InventoryMenu(Client client, Player player) {
        super(player.property);
        this.client = client;
        this.player = player;

        if (player.activeItem != null) {
            player.inventory.items.add(0, player.activeItem);
            player.activeItem = null;
        }
    }

    public void tick() {
        if (input.menu.clicked) {
            sendUpdate();
            game.setMenu(null);
        }

        if (input.up.clicked)
            selected--;
        if (input.down.clicked)
            selected++;

        int len = player.inventory.items.size();
        if (len == 0)
            selected = 0;
        if (selected < 0)
            selected += len;
        if (selected >= len)
            selected -= len;

        if (input.attack.clicked && len > 0) {
            Item item = player.inventory.items.remove(selected);
            player.activeItem = item;
            game.setMenu(null);
            sendUpdate();
        }
    }

    private void sendUpdate() {
        ClientPlayerUpdatePacket packet = new ClientPlayerUpdatePacket();
        packet.player = player;
        client.session().send(packet);
    }

    public void render(ClientScreen screen) {
        Font.renderFrame(screen, "inventory", 1, 1, 12, 11);
        renderItemList(screen, 1, 1, 12, 11, player.inventory.items, selected);
    }
}