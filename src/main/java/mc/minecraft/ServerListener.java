package mc.minecraft;

import mc.api.Session;
import mc.minecraft.data.message.Message;
import mc.minecraft.data.status.PlayerInfo;
import mc.minecraft.data.status.ServerStatusInfo;
import mc.minecraft.data.status.VersionInfo;
import mc.minecraft.data.status.handler.ServerInfoBuilder;
import mc.minecraft.packet.HandshakePacket;
import mc.minecraft.packet.ingame.client.ClientKeepAlivePacket;
import mc.minecraft.packet.ingame.server.ServerDisconnectPacket;
import mc.minecraft.packet.ingame.server.ServerKeepAlivePacket;
import mc.minecraft.packet.login.client.EncryptionResponsePacket;
import mc.minecraft.packet.login.client.LoginStartPacket;
import mc.minecraft.packet.login.server.EncryptionRequestPacket;
import mc.minecraft.packet.login.server.LoginDisconnectPacket;
import mc.minecraft.packet.login.server.LoginSetCompressionPacket;
import mc.minecraft.packet.login.server.LoginSuccessPacket;
import mc.minecraft.packet.status.client.StatusPingPacket;
import mc.minecraft.packet.status.client.StatusQueryPacket;
import mc.minecraft.packet.status.server.StatusPongPacket;
import mc.minecraft.packet.status.server.StatusResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.net.Proxy;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

final class ServerListener extends Session.ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerListener.class);
    private static final KeyPair KEY_PAIR = mc.minecraft.crypt.Util.generateKeyPair();

    private byte verifyToken[] = new byte[4];
    private String serverId = "";

    private long lastPingTime = 0;
    private int lastPingId = 0;

    ServerListener() {
        new Random().nextBytes(this.verifyToken);
    }

    @Override
    public void connected(Session.Event event) {
        event.session.setFlag(Constants.PING_KEY, 0);
    }

    @Override
    public void packetReceived(Session.Event event) {
        MinecraftProtocol protocol = (MinecraftProtocol) event.session.protocol();
        if (protocol.getSub() == MinecraftProtocol.Sub.HANDSHAKE) {
            if (event.packet() instanceof HandshakePacket) {
                HandshakePacket packet = event.asPacket();
                switch (packet.getIntent()) {
                    case STATUS:
                        protocol.setSub(MinecraftProtocol.Sub.STATUS, false, event.session);
                        break;
                    case LOGIN:
                        protocol.setSub(MinecraftProtocol.Sub.LOGIN, false, event.session);
                        if (packet.getProtocolVersion() > Constants.PROTOCOL_VERSION) {
                            event.session.disconnect("Outdated server! I'm still on " + Constants.GAME_VERSION + ".");
                        } else if (packet.getProtocolVersion() < Constants.PROTOCOL_VERSION) {
                            event.session.disconnect("Outdated client! Please use " + Constants.GAME_VERSION + ".");
                        }

                        break;
                    default:
                        throw new UnsupportedOperationException("Invalid client intent: " + packet.getIntent());
                }
            }
        }

        if (protocol.getSub() == MinecraftProtocol.Sub.LOGIN) {
            if (event.packet() instanceof LoginStartPacket) {
                String username = event.<LoginStartPacket>asPacket().getUsername();
                event.session.setFlag(Constants.USERNAME_KEY, username);
                boolean verify = event.session.hasFlag(Constants.VERIFY_USERS_KEY) ?
                        event.session.<Boolean>flag(Constants.VERIFY_USERS_KEY) : true;
                if (verify) {
                    event.session.send(new EncryptionRequestPacket(this.serverId, KEY_PAIR.getPublic(), this.verifyToken));
                } else {
                    new Thread(new UserAuthTask(event.session, null)).start();
                }
            } else if (event.packet() instanceof EncryptionResponsePacket) {
                EncryptionResponsePacket packet = event.asPacket();
                PrivateKey privateKey = KEY_PAIR.getPrivate();
                if (!Arrays.equals(this.verifyToken, packet.getVerifyToken(privateKey))) {
                    event.session.disconnect("Invalid nonce!");
                    return;
                }

                SecretKey key = packet.getSecretKey(privateKey);
                protocol.enableEncryption(key);
                new Thread(new UserAuthTask(event.session, key)).start();
            }
        }

        if (protocol.getSub() == MinecraftProtocol.Sub.STATUS) {
            if (event.packet() instanceof StatusQueryPacket) {
                ServerInfoBuilder builder = event.session.flag(Constants.SERVER_INFO_BUILDER_KEY);
                if (builder == null) {
                    builder = session -> new ServerStatusInfo(VersionInfo.CURRENT,
                            new PlayerInfo(0, 20, new Profile[]{}),
                            Message.fromString("A Minecraft Server"), null);
                }

                ServerStatusInfo info = builder.buildInfo(event.session);
                event.session.send(new StatusResponsePacket(info));
            } else if (event.packet() instanceof StatusPingPacket) {
                event.session.send(new StatusPongPacket(event.<StatusPingPacket>asPacket().getPingTime()));
            }
        }

        if (protocol.getSub() == MinecraftProtocol.Sub.GAME) {
            if (event.packet() instanceof ClientKeepAlivePacket) {
                ClientKeepAlivePacket packet = event.asPacket();
                if (packet.getPingId() == this.lastPingId) {
                    long time = System.currentTimeMillis() - this.lastPingTime;
                    event.session.setFlag(Constants.PING_KEY, time);
                }
            }
        }
//        logger.info("Server recv: " + event.packet());
    }

    @Override
    public void packetSent(Session.Event event) {
//        logger.info("Server sent: " + event.packet());
    }

    @Override
    public void disconnecting(Session.DisconnectEvent event) {
        MinecraftProtocol protocol = (MinecraftProtocol) event.session.protocol();
        if (protocol.getSub() == MinecraftProtocol.Sub.LOGIN) {
            event.session.send(new LoginDisconnectPacket(event.reason));
        } else if (protocol.getSub() == MinecraftProtocol.Sub.GAME) {
            event.session.send(new ServerDisconnectPacket(event.reason));
        }
    }

    private final class UserAuthTask implements Runnable {
        private final Session session;
        private final SecretKey key;

        UserAuthTask(Session session, SecretKey key) {
            this.key = key;
            this.session = session;
        }

        @Override
        public void run() {
            String username = this.session.flag(Constants.USERNAME_KEY);
            boolean verify = this.session.hasFlag(Constants.VERIFY_USERS_KEY) ? this.session.<Boolean>flag(Constants.VERIFY_USERS_KEY) : true;

            Profile profile;
            if (verify && this.key != null) {
                Proxy proxy = this.session.<Proxy>flag(Constants.AUTH_PROXY_KEY);
                if (proxy == null) {
                    proxy = Proxy.NO_PROXY;
                }
                profile = new Profile(UUID.randomUUID(), username);
                /*try {
                    profile = new SessionService(proxy).getProfileByServer(username, new BigInteger(Util.getServerIdHash(serverId, KEY_PAIR.getPublic(), this.key)).toString(16));
                } catch(RequestException e) {
                    this.session.disconnect("Failed to make session service request.", e);
                    return;
                }*/

                if (profile == null) {
                    this.session.disconnect("Failed to verify username.");
                }
            } else {
                profile = new Profile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes()), username);
            }

            int threshold;
            if (this.session.hasFlag(Constants.SERVER_COMPRESSION_THRESHOLD)) {
                threshold = this.session.flag(Constants.SERVER_COMPRESSION_THRESHOLD);
            } else {
                threshold = 256;
            }

            this.session.send(new LoginSetCompressionPacket(threshold));
            this.session.setCompressionThreshold(threshold);
            this.session.send(new LoginSuccessPacket(profile));
            this.session.setFlag(Constants.PROFILE_KEY, profile);
            ((MinecraftProtocol) this.session.protocol()).setSub(MinecraftProtocol.Sub.GAME, false, this.session);
            ServerLoginHandler handler = this.session.flag(Constants.SERVER_LOGIN_HANDLER_KEY);
            if (handler != null) {
                handler.loggedIn(this.session);
            }
            new Thread(new KeepAliveTask(this.session)).start();
        }
    }

    private final class KeepAliveTask implements Runnable {
        private final Session session;

        KeepAliveTask(Session session) {
            this.session = session;
        }

        @Override
        public void run() {
            while (this.session.isConnected()) {
                lastPingTime = System.currentTimeMillis();
                lastPingId = (int) lastPingTime;
                this.session.send(new ServerKeepAlivePacket(lastPingId));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
