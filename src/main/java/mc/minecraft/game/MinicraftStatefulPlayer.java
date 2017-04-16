package mc.minecraft.game;

import mc.api.Server;
import mc.api.Session;
import mc.minecraft.Constants;
import mc.minecraft.StatefulPlayer;
import mc.minecraft.Profile;
import mc.minecraft.data.game.MessageType;
import mc.minecraft.data.message.ChatColor;
import mc.minecraft.data.message.Message;
import mc.minecraft.data.message.MessageStyle;
import mc.minecraft.data.message.TextMessage;
import mc.minecraft.notch.level.Level;
import mc.minecraft.notch.level.tile.Tile;
import mc.minecraft.notch.property.PropertyContainer;
import mc.minecraft.packet.ingame.client.ClientChatPacket;
import mc.minecraft.packet.ingame.server.ServerChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

final class MinicraftStatefulPlayer extends Session.ListenerAdapter implements StatefulPlayer {
    private static final Logger logger = LoggerFactory.getLogger(MinicraftStatefulPlayer.class);
    protected final Random random = new Random();
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
        //level.add(this);
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
}
