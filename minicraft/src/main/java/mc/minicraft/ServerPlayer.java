package mc.minicraft;

import mc.api.Server;
import mc.api.Session;
import mc.api.Sound;
import mc.engine.property.PropertyContainer;
import mc.minicraft.data.game.MessageType;
import mc.minicraft.data.message.ChatColor;
import mc.minicraft.data.message.Message;
import mc.minicraft.data.message.MessageStyle;
import mc.minicraft.data.message.TextMessage;
import mc.minicraft.engine.crafting.Recipe;
import mc.minicraft.engine.entity.Inventory;
import mc.minicraft.engine.entity.Player;
import mc.minicraft.engine.entity.PlayerHandler;
import mc.minicraft.engine.level.ServerLevel;
import mc.minicraft.engine.level.tile.Tile;
import mc.minicraft.packet.ingame.client.ClientChatPacket;
import mc.minicraft.packet.ingame.client.player.ClientPlayerPositionPacket;
import mc.minicraft.packet.ingame.server.ServerChatPacket;
import mc.minicraft.packet.ingame.server.ServerSoundEffectPacket;
import mc.minicraft.packet.ingame.server.ServerUpdateEntityPacket;
import mc.minicraft.packet.ingame.server.level.ServerChangeLevelPacket;
import mc.minicraft.packet.ingame.server.level.ServerStartLevelPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public final class ServerPlayer extends Session.ListenerAdapter implements Comparable<ServerPlayer>, Sound {
    private static final Logger logger = LoggerFactory.getLogger(ServerPlayer.class);
    public static final int START_LEVEL = 3;
    protected final Random random = new Random();
    final Server server;
    final Session session;
    private final UUID id;
    private final PropertyContainer container;
    public final Player player;
    private long gameTime;
    private int deadTime;
    private int wonTime;
    private int currentLevel;
    private ServerLevel level;
    int pendingLevelChange;
    public int visibleDistance;

    private final PlayerState state = new PlayerState();
    private final ServerLevel[] levels;

    public ServerPlayer(Session session, Server server, PropertyContainer container, ServerLevel[] levels, int currentLevel) {
        this.session = session;
        this.levels = levels;
        this.currentLevel = currentLevel;
        this.id = UUID.randomUUID();
        this.server = server;
        this.container = container;
        this.player = new Player(this, state, container);
        this.visibleDistance = 10;
        this.level = levels[currentLevel];
    }

    @Override
    public void packetReceived(Session.Event event) {
        if (event.packet() instanceof ClientChatPacket) {
            ClientChatPacket packet = event.asPacket();
            Profile profile = event.session.flag(Constants.PROFILE_KEY);
            Message msg = new TextMessage(String.format("[%s] ", profile.name)).setStyle(
                    new MessageStyle().setColor(ChatColor.YELLOW));
            msg.addExtra(new TextMessage(packet.getMessage()));
            server.sendBroadcast(new ServerChatPacket(msg, MessageType.CHAT), event.session);
            logger.info(profile.name + ": " + packet.getMessage());
        }
    }

    public void tick() {
        gameTime++;
        player.tick();
        state.reset();
    }

    public void removePlayer() {
        if (level != null)
            level.remove(player);
    }

    public void registerPlayer(ServerLevel level) {
        this.gameTime = 0;
        this.deadTime = 0;
        this.wonTime = 0;
        this.level = level;
        this.findStartPos(level);
        level.add(player);
    }

    private boolean findStartPos(ServerLevel level) {
        while (true) {
            int x = random.nextInt(level.w);
            int y = random.nextInt(level.h);
            if (level.getTile(x, y) == Tile.grass) {
                player.x = x * 16 + 8;
                player.y = y * 16 + 8;
                return true;
            }
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerPlayer) {
            return Objects.equals(((ServerPlayer) obj).id, id);
        }
        return this.equals(obj);
    }

    @Override
    public int compareTo(ServerPlayer o) {
        return id.compareTo(o.id);
    }

    @Override
    public void play(int x, int y, Type type) {
        System.out.printf("SP\n");
        ServerSoundEffectPacket packet = new ServerSoundEffectPacket();
        packet.type = type;
        session.send(packet);
    }

    void updateViewport() {
        //FIXME: Обновляем видимое окружение
    }

    public void update(ClientPlayerPositionPacket update) {
        state.xa = update.xa;
        state.ya = update.ya;
    }

    public void attack() {
        state.attack = true;
    }

    public void update(Player player) {
        this.player.inventory.items.clear();
        this.player.inventory.items.addAll(player.inventory.items);
        this.player.activeItem = player.activeItem;
    }

    public void process(Session session) {
        if (level != null) {
            level.process(session, this);
            if (player.updated) {
                ServerUpdateEntityPacket packet = new ServerUpdateEntityPacket();
                packet.entity = player;
                session.send(packet);
                player.updated = false;
            }
        }
    }

    void respawn(UUID id) {
        registerPlayer(levels[START_LEVEL]);
        session.send(new ServerStartLevelPacket(levels[START_LEVEL], this));
    }

    private final class PlayerState implements PlayerHandler {

        int xa;
        int ya;
        boolean attack;

        void reset() {
            xa = 0;
            ya = 0;
            attack = false;
        }

        @Override
        public Point move() {
            return new Point(xa, ya);
        }

        @Override
        public boolean isAttacked() {
            return attack;
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
            if (level != null) {
                UUID ido = level.id;
                level.remove(player);
                currentLevel += dir;
                level = levels[currentLevel];
                System.out.printf("%s -> %s\n", ido, level.id);
                player.x = (player.x >> 4) * 16 + 8;
                player.y = (player.y >> 4) * 16 + 8;
                level.add(player);
                session.send(new ServerChangeLevelPacket(level, ServerPlayer.this, currentLevel));
            }
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
}
