package mc;


import mc.api.Client;
import mc.api.Server;
import mc.api.Session;
import mc.minecraft.Constants;
import mc.minecraft.MinecraftProtocol;
import mc.minecraft.Profile;
import mc.minecraft.ServerLoginHandler;
import mc.minecraft.data.game.entity.player.GameMode;
import mc.minecraft.data.game.setting.Difficulty;
import mc.minecraft.data.game.world.WorldType;
import mc.minecraft.data.message.*;
import mc.minecraft.data.status.PlayerInfo;
import mc.minecraft.data.status.ServerStatusInfo;
import mc.minecraft.data.status.VersionInfo;
import mc.minecraft.data.status.handler.ServerInfoBuilder;
import mc.minecraft.data.status.handler.ServerInfoHandler;
import mc.minecraft.data.status.handler.ServerPingTimeHandler;
import mc.minecraft.packet.ingame.client.ClientChatPacket;
import mc.minecraft.packet.ingame.server.ServerChatPacket;
import mc.minecraft.packet.ingame.server.ServerJoinGamePacket;
import mc.engine.tcp.DefaultClient;
import mc.engine.tcp.DefaultFactory;
import mc.engine.tcp.DefaultServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.Arrays;

//-Dmaven.test.skip=true
public final class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final DefaultFactory FACTORY = DefaultFactory.instance();
    private static final boolean SPAWN_SERVER = true;
    private static final boolean VERIFY_USERS = true;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 25565;
    private static final Proxy PROXY = Proxy.NO_PROXY;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";

    public static void main(String[] args) {
        if (SPAWN_SERVER) {
            Server server = new DefaultServer(HOST, PORT, MinecraftProtocol.class, FACTORY.newSessionFactory(PROXY));
            server.setGlobalFlag(Constants.AUTH_PROXY_KEY, AUTH_PROXY);
            server.setGlobalFlag(Constants.VERIFY_USERS_KEY, VERIFY_USERS);
            server.setGlobalFlag(Constants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session ->
                    new ServerStatusInfo(new VersionInfo(Constants.GAME_VERSION,
                            Constants.PROTOCOL_VERSION),
                            new PlayerInfo(100, 0, new Profile[0]), new TextMessage("Hello world!"), null));

            server.setGlobalFlag(Constants.SERVER_LOGIN_HANDLER_KEY,
                    (ServerLoginHandler) session -> session.send(new ServerJoinGamePacket(0, false,
                            GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10,
                            WorldType.DEFAULT, false)));

            server.setGlobalFlag(Constants.SERVER_COMPRESSION_THRESHOLD, 100);
            server.addListener(new Server.ListenerAdapter() {
                public void sessionAdded(Server.Event event) {
                    event.session.addListener(new Session.ListenerAdapter() {
                        public void packetReceived(Session.Event event) {
                            if (event.packet() instanceof ClientChatPacket) {
                                ClientChatPacket packet = event.asPacket();
                                Profile profile = event.session.flag(Constants.PROFILE_KEY);
                                logger.info(profile.name + ": " + packet.getMessage());
                                Message msg = new TextMessage("Hello, ").setStyle(
                                        new MessageStyle().setColor(ChatColor.GREEN));
                                Message name = new TextMessage(profile.name).setStyle(
                                        new MessageStyle().setColor(ChatColor.AQUA).addFormat(ChatFormat.UNDERLINED));
                                Message end = new TextMessage("!");
                                msg.addExtra(name);
                                msg.addExtra(end);
                                event.session.send(new ServerChatPacket(msg));
                            }
                        }
                    });
                }

                @Override
                public void sessionRemoved(Server.Event event) {
                    MinecraftProtocol protocol = (MinecraftProtocol) event.session.protocol();
                    if (protocol.getSub() == MinecraftProtocol.Sub.GAME) {
                        logger.info("Closing server.");
                        event.server.close();
                    }
                }
            });
            server.bind();
        }
        status();
        login();
    }

    private static void status() {
        MinecraftProtocol protocol = new MinecraftProtocol(MinecraftProtocol.Sub.STATUS);
        Client client = new DefaultClient(HOST, PORT, protocol, FACTORY.newSessionFactory(PROXY));
        client.session().setFlag(Constants.AUTH_PROXY_KEY, AUTH_PROXY);
        client.session().setFlag(Constants.SERVER_INFO_HANDLER_KEY, (ServerInfoHandler) (session, info) -> {
            logger.info("Version: " + info.getVersionInfo().getVersionName() + ", " +
                    info.getVersionInfo().getProtocolVersion());
            logger.info("Player Count: " + info.getPlayerInfo().getOnlinePlayers() + " / " +
                    info.getPlayerInfo().getMaxPlayers());
            logger.info("Players: " + Arrays.toString(info.getPlayerInfo().getPlayers()));
            logger.info("Description: " + info.getDescription().getFullText());
            logger.info("Icon: " + info.getIcon());
        });

        client.session().setFlag(Constants.SERVER_PING_TIME_HANDLER_KEY,
                (ServerPingTimeHandler) (session, pingTime) ->
                        logger.info("Server ping took " + pingTime + "ms"));

        client.session().connect();
        while (client.session().isConnected()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void login() {
        MinecraftProtocol protocol;
        if (VERIFY_USERS) {
            try {
                protocol = new MinecraftProtocol(USERNAME, PASSWORD, false);
                logger.info("Successfully authenticated user.");
            } catch (Exception e) {
                logger.error("", e);
                return;
            }
        } else {
            protocol = new MinecraftProtocol(USERNAME);
        }

        Client client = new DefaultClient(HOST, PORT, protocol, FACTORY.newSessionFactory(PROXY));
        client.session().setFlag(Constants.AUTH_PROXY_KEY, AUTH_PROXY);
        client.session().addListener(new Session.ListenerAdapter() {
            @Override
            public void packetReceived(Session.Event event) {
                if (event.packet() instanceof ServerJoinGamePacket) {
                    event.session.send(new ClientChatPacket("Hello, this is a test"));
                } else if (event.packet() instanceof ServerChatPacket) {
                    Message message = event.<ServerChatPacket>asPacket().getMessage();
                    logger.info("Received Message: " + message.getFullText());
                    if (message instanceof TranslationMessage) {
                        logger.info("Received Translation Components: " +
                                Arrays.toString(((TranslationMessage) message).getTranslationParams()));
                    }
                    event.session.disconnect("Finished");
                }
            }

            @Override
            public void disconnected(Session.DisconnectEvent event) {
                logger.info("Disconnected: " + Message.fromString(event.reason).getFullText());
                if (event.cause != null) {
                    event.cause.printStackTrace();
                }
            }
        });

        client.session().connect();
        while (client.session().isConnected()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
