package mc.minecraft.game;

import mc.api.Server;
import mc.api.Session;
import mc.engine.tcp.DefaultFactory;
import mc.engine.tcp.DefaultServer;
import mc.minecraft.*;
import mc.minecraft.data.game.MessageType;
import mc.minecraft.data.game.entity.player.GameMode;
import mc.minecraft.data.game.setting.Difficulty;
import mc.minecraft.data.game.world.WorldType;
import mc.minecraft.data.message.ChatColor;
import mc.minecraft.data.message.Message;
import mc.minecraft.data.message.MessageStyle;
import mc.minecraft.data.message.TextMessage;
import mc.minecraft.data.status.PlayerInfo;
import mc.minecraft.data.status.ServerStatusInfo;
import mc.minecraft.data.status.VersionInfo;
import mc.minecraft.data.status.handler.ServerInfoBuilder;
import mc.minecraft.notch.level.Level;
import mc.minecraft.notch.level.tile.Tile;
import mc.minecraft.notch.property.DefaultPropertyContainer;
import mc.minecraft.notch.property.PropertyConstants;
import mc.minecraft.notch.property.PropertyContainer;
import mc.minecraft.packet.ingame.server.ServerChatPacket;
import mc.minecraft.packet.ingame.server.ServerJoinGamePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class MinicraftGameServer extends Server.ListenerAdapter implements ServerLoginHandler, ServerInfoBuilder, GameServer, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);
    private static final DefaultFactory FACTORY = DefaultFactory.instance();
    private static final boolean VERIFY_USERS = true;
    private static final Proxy PROXY = Proxy.NO_PROXY;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;

    private final PropertyContainer container = new DefaultPropertyContainer();
    private final Set<StatefulPlayer> states = new ConcurrentSkipListSet<>();
    private final Server server;

    private MinicraftGameServer() {
        this.server = new DefaultServer(
                PropertyConstants.SERVER_HOSTNAME_DEFAULT,
                PropertyConstants.SERVER_PORT_DEFAULT,
                MinicraftProtocol.class, FACTORY.newSessionFactory(PROXY));
        this.server.setGlobalFlag(Constants.AUTH_PROXY_KEY, AUTH_PROXY);
        this.server.setGlobalFlag(Constants.VERIFY_USERS_KEY, VERIFY_USERS);
        this.server.setGlobalFlag(Constants.SERVER_INFO_BUILDER_KEY, this);
        this.server.setGlobalFlag(Constants.SERVER_LOGIN_HANDLER_KEY, this);
        this.server.setGlobalFlag(Constants.SERVER_COMPRESSION_THRESHOLD, 100);
        this.server.addListener(this);
    }

    public static GameServer createServer() {
        return new MinicraftGameServer();
    }

    public void start() {
        server.bind(false);
    }

    @Override
    public void serverBound(Server.Event event) {
        running.set(true);
        new Thread(this).start();
    }

    @Override
    public void sessionAdded(Server.Event event) {
        MinicraftStatefulPlayer statefulPlayer = new MinicraftStatefulPlayer(server, container);
        event.session.addListener(statefulPlayer);
        event.session.setFlag(Constants.GAME_PLAYER_KEY, statefulPlayer);
        states.add(statefulPlayer);
        registerPlayer(statefulPlayer);
    }

    @Override
    public void sessionRemoved(Server.Event event) {
        MinicraftProtocol protocol = (MinicraftProtocol) event.session.protocol();
        if (protocol.getSub() == MinicraftProtocol.Sub.GAME) {
            Profile profile = event.session.flag(Constants.PROFILE_KEY);
            Message message = new TextMessage("[NOTIFY] ").setStyle(
                    new MessageStyle().setColor(ChatColor.DARK_AQUA));
            message.addExtra(new TextMessage(profile.name + " left").setStyle(
                    new MessageStyle().setColor(ChatColor.DARK_GRAY)));
            server.sendBroadcast(new ServerChatPacket(message, MessageType.CHAT), event.session);
            StatefulPlayer engine = event.session.flag(Constants.GAME_PLAYER_KEY);
            states.remove(engine);
        }
    }

    @Override
    public void loggedIn(Session session) {
        Profile profile = session.flag(Constants.PROFILE_KEY);
        Message message = new TextMessage("[NOTIFY] ").setStyle(
                new MessageStyle().setColor(ChatColor.DARK_AQUA));
        message.addExtra(new TextMessage(profile.name + " joined").setStyle(
                new MessageStyle().setColor(ChatColor.DARK_GRAY)));
        server.sendBroadcast(new ServerChatPacket(message, MessageType.CHAT), session);
        session.send(new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10,
                WorldType.DEFAULT, false));
    }

    @Override
    public ServerStatusInfo buildInfo(Session session) {
        List<Profile> profiles = server.sessions()
                .map(s -> s.<Profile>flag(Constants.PROFILE_KEY)).collect(Collectors.toList());
        return new ServerStatusInfo(new VersionInfo(Constants.GAME_VERSION,
                Constants.PROTOCOL_VERSION),
                new PlayerInfo(100, profiles.size(), profiles.toArray(new Profile[profiles.size()])),
                new TextMessage("Hello world!"), null);
    }

    private final AtomicBoolean running = new AtomicBoolean(true);

    private Level level;
    private Level[] levels = new Level[5];
    private int tickCount;

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / 60;
        int frames = 0;
        int ticks = 0;
        long lastTimer1 = System.currentTimeMillis();

        init();
        while (running.get()) {
            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;
            while (unprocessed >= 1) {
                ticks++;
                tick();
                unprocessed -= 1;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                updateFramesInfo(frames, ticks);
                frames = 0;
                ticks = 0;
            }
        }
    }

    private void registerPlayer(MinicraftStatefulPlayer engine) {
        engine.registerPlayer(level);
    }

    private void resetGame() {
        levels = new Level[5];
        int startLevel = 3;

        levels[4] = new Level(128, 128, 1, null);
        levels[3] = new Level(128, 128, 0, levels[4]);
        levels[2] = new Level(128, 128, -1, levels[3]);
        levels[1] = new Level(128, 128, -2, levels[2]);
        levels[0] = new Level(128, 128, -3, levels[1]);

        level = levels[startLevel];

        states.forEach(engine -> {
            engine.resetPlayer(level);
        });

        for (int i = 0; i < 5; i++) {
            levels[i].trySpawn(5000);
        }
    }

    private void init() {
        resetGame();
    }

    private void tick() {
        tickCount++;
//        if (!hasFocus()) {
//            input.releaseAll();
//        } else {
//            if (!player.removed && !hasWon) gameTime++;
//
//            input.tick();
//            if (menu != null) {
//                menu.tick();
//            } else {
//                if (player.removed) {
//                    playerDeadTime++;
//                    if (playerDeadTime > 60) {
//                        setMenu(new DeadMenu(titleMenu));
//                    }
//                } else {
//                    if (pendingLevelChange != 0) {
//                        setMenu(new LevelTransitionMenu(pendingLevelChange, container));
//                        pendingLevelChange = 0;
//                    }
//                }
//                if (wonTimer > 0) {
//                    if (--wonTimer == 0) {
//                        setMenu(new WonMenu(titleMenu));
//                    }
//                }
        for (int i = 0; i < 5; i++) {
            levels[i].tick();
        }
        Tile.tickCount++;//FIXME: Wat?
//            }
//        }
        states.forEach(StatefulPlayer::tick);
    }

    private void updateFramesInfo(int frames, int ticks) {
        logger.debug(String.format("Ticks: %d", ticks));
    }
}
