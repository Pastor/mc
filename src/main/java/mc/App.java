package mc;

import mc.api.Server;
import mc.api.Session;
import mc.engine.tcp.DefaultFactory;
import mc.engine.tcp.DefaultServer;
import mc.minecraft.Constants;
import mc.minecraft.MinicraftProtocol;
import mc.minecraft.Profile;
import mc.minecraft.ServerLoginHandler;
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
import mc.minecraft.notch.MinicraftGame;
import mc.minecraft.notch.property.PropertyConstants;
import mc.minecraft.packet.ingame.client.ClientChatPacket;
import mc.minecraft.packet.ingame.server.ServerChatPacket;
import mc.minecraft.packet.ingame.server.ServerJoinGamePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;

//-Dmaven.test.skip=true
public final class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final DefaultFactory FACTORY = DefaultFactory.instance();
    private static final boolean SPAWN_SERVER = true;
    private static final boolean VERIFY_USERS = true;
    private static final Proxy PROXY = Proxy.NO_PROXY;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;

    public static void main(String[] args) {
        if (SPAWN_SERVER) {
            Server server = new DefaultServer(
                    PropertyConstants.SERVER_HOSTNAME_DEFAULT,
                    PropertyConstants.SERVER_PORT_DEFAULT,
                    MinicraftProtocol.class, FACTORY.newSessionFactory(PROXY));
            server.setGlobalFlag(Constants.AUTH_PROXY_KEY, AUTH_PROXY);
            server.setGlobalFlag(Constants.VERIFY_USERS_KEY, VERIFY_USERS);
            server.setGlobalFlag(Constants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session ->
                    new ServerStatusInfo(new VersionInfo(Constants.GAME_VERSION,
                            Constants.PROTOCOL_VERSION),
                            new PlayerInfo(100, 0, new Profile[0]), new TextMessage("Hello world!"), null));

            server.setGlobalFlag(Constants.SERVER_LOGIN_HANDLER_KEY,
                    (ServerLoginHandler) session -> {
                        Profile profile = session.flag(Constants.PROFILE_KEY);
                        Message message = new TextMessage("[NOTIFY] ").setStyle(
                                new MessageStyle().setColor(ChatColor.DARK_AQUA));
                        message.addExtra(new TextMessage(profile.name + " joined").setStyle(
                                new MessageStyle().setColor(ChatColor.DARK_GRAY)));
                        server.sendBroadcast(new ServerChatPacket(message, MessageType.CHAT), session);
                        session.send(new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10,
                                WorldType.DEFAULT, false));
                    });

            server.setGlobalFlag(Constants.SERVER_COMPRESSION_THRESHOLD, 100);
            server.addListener(new Server.ListenerAdapter() {
                public void sessionAdded(Server.Event event) {
                    event.session.addListener(new Session.ListenerAdapter() {
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
                    });
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
                    }
                }
            });
            server.bind(false);
        }
        startGame();
        startGame();
//        startGame("Maine", "Password2");
    }

    private static void startGame() {
        MinicraftGame.startGame(PROXY, true);
    }
}
