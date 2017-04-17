package mc.minicraft.server;

import mc.api.Server;
import mc.api.Session;
import mc.engine.property.PropertyContainer;
import mc.minicraft.Constants;
import mc.minicraft.Profile;
import mc.minicraft.StatefulPlayer;
import mc.minicraft.component.level.Level;
import mc.minicraft.component.level.tile.Tile;
import mc.minicraft.data.game.MessageType;
import mc.minicraft.data.message.ChatColor;
import mc.minicraft.data.message.Message;
import mc.minicraft.data.message.MessageStyle;
import mc.minicraft.data.message.TextMessage;
import mc.minicraft.packet.ingame.client.ClientChatPacket;
import mc.minicraft.packet.ingame.server.ServerChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

final class MinicraftStatefulPlayer extends Session.ListenerAdapter implements
        StatefulPlayer, Comparable<MinicraftStatefulPlayer> {
    private static final Logger logger = LoggerFactory.getLogger(MinicraftStatefulPlayer.class);
    protected final Random random = new Random();
    private final UUID id;
    private final PropertyContainer container;
    private long gameTime;
    private int deadTime;
    private int wonTime;
    private int currentLevel;
    private Level level;
    int pendingLevelChange;

    public int x, y;
    private final Server server;

    MinicraftStatefulPlayer(Server server, PropertyContainer container) {
        this.server = server;
        this.container = container;
        this.id = UUID.randomUUID();
    }

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

    @Override
    public void tick() {
        gameTime++;
    }

    @Override
    public void resetPlayer(Level level) {
        this.gameTime = 0;
        this.deadTime = 0;
        this.wonTime = 0;
        this.findStartPos(level);
        registerPlayer(level);
    }

    @Override
    public void registerPlayer(Level level) {
//        level.add(this);
        this.level = level;
    }

    public void changeLevel(Level[] levels, int dir) {
//        level.remove(player);
        currentLevel += dir;
        level = levels[currentLevel];
        x = (x >> 4) * 16 + 8;
        y = (y >> 4) * 16 + 8;
//        level.add(player);
    }
//
    private boolean findStartPos(Level level) {
        while (true) {
            int x = random.nextInt(level.w);
            int y = random.nextInt(level.h);
            if (level.getTile(x, y) == Tile.grass) {
                this.x = x * 16 + 8;
                this.y = y * 16 + 8;
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
        if (obj instanceof MinicraftStatefulPlayer) {
            return Objects.equals(((MinicraftStatefulPlayer) obj).id, id);
        }
        return this.equals(obj);
    }

    @Override
    public int compareTo(MinicraftStatefulPlayer o) {
        return id.compareTo(o.id);
    }
}
