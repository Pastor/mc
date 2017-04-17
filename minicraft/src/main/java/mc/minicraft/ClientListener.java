package mc.minicraft;

import mc.api.Session;
import mc.minicraft.crypt.Util;
import mc.minicraft.data.status.ServerStatusInfo;
import mc.minicraft.data.status.handler.ServerInfoHandler;
import mc.minicraft.data.status.handler.ServerPingTimeHandler;
import mc.minicraft.packet.HandshakePacket;
import mc.minicraft.packet.ingame.client.ClientKeepAlivePacket;
import mc.minicraft.packet.ingame.server.ServerDisconnectPacket;
import mc.minicraft.packet.ingame.server.ServerKeepAlivePacket;
import mc.minicraft.packet.ingame.server.ServerSetCompressionPacket;
import mc.minicraft.packet.login.client.EncryptionResponsePacket;
import mc.minicraft.packet.login.client.LoginStartPacket;
import mc.minicraft.packet.login.server.EncryptionRequestPacket;
import mc.minicraft.packet.login.server.LoginDisconnectPacket;
import mc.minicraft.packet.login.server.LoginSetCompressionPacket;
import mc.minicraft.packet.login.server.LoginSuccessPacket;
import mc.minicraft.packet.status.client.StatusPingPacket;
import mc.minicraft.packet.status.client.StatusQueryPacket;
import mc.minicraft.packet.status.server.StatusPongPacket;
import mc.minicraft.packet.status.server.StatusResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.Proxy;

final class ClientListener extends Session.ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClientListener.class);

    @Override
    public void packetReceived(Session.Event event) {
        MinicraftProtocol protocol = (MinicraftProtocol) event.session.protocol();
        if (protocol.getSub() == MinicraftProtocol.Sub.LOGIN) {
            if (event.packet() instanceof EncryptionRequestPacket) {
                EncryptionRequestPacket packet = event.asPacket();
                SecretKey key = Util.generateSharedKey();

                Proxy proxy = event.session.<Proxy>flag(Constants.AUTH_PROXY_KEY);
                if (proxy == null) {
                    proxy = Proxy.NO_PROXY;
                }

                Profile profile = event.session.flag(Constants.PROFILE_KEY);
                String serverHash = new BigInteger(Util.getServerIdHash(packet.getServerId(), packet.getPublicKey(), key)).toString(16);
                String accessToken = event.session.flag(Constants.ACCESS_TOKEN_KEY);
                /*try {
                    new SessionService(proxy).joinServer(profile, accessToken, serverHash);
                } catch (ServiceUnavailableException e) {
                    event.session.disconnect("Login failed: Authentication service unavailable.", e);
                    return;
                } catch (InvalidCredentialsException e) {
                    event.session.disconnect("Login failed: Invalid login session.", e);
                    return;
                } catch (RequestException e) {
                    event.session.disconnect("Login failed: Authentication error: " + e.getMessage(), e);
                    return;
                }*/

                event.session.send(new EncryptionResponsePacket(key, packet.getPublicKey(), packet.getVerifyToken()));
                protocol.enableEncryption(key);
            } else if (event.packet() instanceof LoginSuccessPacket) {
                LoginSuccessPacket packet = event.asPacket();
                event.session.setFlag(Constants.PROFILE_KEY, packet.getProfile());
                protocol.setSub(MinicraftProtocol.Sub.GAME, true, event.session);
            } else if (event.packet() instanceof LoginDisconnectPacket) {
                LoginDisconnectPacket packet = event.asPacket();
                event.session.disconnect(packet.getReason().getFullText());
            } else if (event.packet() instanceof LoginSetCompressionPacket) {
                event.session.setCompressionThreshold(event.<LoginSetCompressionPacket>asPacket().getThreshold());
            }
        } else if (protocol.getSub() == MinicraftProtocol.Sub.STATUS) {
            if (event.packet() instanceof StatusResponsePacket) {
                ServerStatusInfo info = event.<StatusResponsePacket>asPacket().getInfo();
                ServerInfoHandler handler = event.session.flag(Constants.SERVER_INFO_HANDLER_KEY);
                if (handler != null) {
                    handler.handle(event.session, info);
                }

                event.session.send(new StatusPingPacket(System.currentTimeMillis()));
            } else if (event.packet() instanceof StatusPongPacket) {
                long time = System.currentTimeMillis() - event.<StatusPongPacket>asPacket().getPingTime();
                ServerPingTimeHandler handler = event.session.flag(Constants.SERVER_PING_TIME_HANDLER_KEY);
                if (handler != null) {
                    handler.handle(event.session, time);
                }

                event.session.disconnect("Finished");
            }
        } else if (protocol.getSub() == MinicraftProtocol.Sub.GAME) {
            if (event.packet() instanceof ServerKeepAlivePacket) {
                event.session.send(new ClientKeepAlivePacket(event.<ServerKeepAlivePacket>asPacket().getPingId()));
            } else if (event.packet() instanceof ServerDisconnectPacket) {
                event.session.disconnect(event.<ServerDisconnectPacket>asPacket().getReason().getFullText());
            } else if (event.packet() instanceof ServerSetCompressionPacket) {
                event.session.setCompressionThreshold(event.<ServerSetCompressionPacket>asPacket().getThreshold());
            }
        }
        logger.info("Client recv: " + event.packet());
    }

    @Override
    public void packetSent(Session.Event event) {
        logger.info("Client sent: " + event.packet());
    }

    public void connected(Session.Event event) {
        MinicraftProtocol protocol = (MinicraftProtocol) event.session.protocol();
        if (protocol.getSub() == MinicraftProtocol.Sub.LOGIN) {
            Profile profile = event.session.flag(Constants.PROFILE_KEY);
            protocol.setSub(MinicraftProtocol.Sub.HANDSHAKE, true, event.session);
            event.session.send(new HandshakePacket(
                    Constants.PROTOCOL_VERSION,
                    event.session.host(),
                    event.session.port(),
                    MinicraftProtocol.HandshakeIntent.LOGIN));
            protocol.setSub(MinicraftProtocol.Sub.LOGIN, true, event.session);
            event.session.send(new LoginStartPacket(profile != null ? profile.name : ""));
        } else if (protocol.getSub() == MinicraftProtocol.Sub.STATUS) {
            protocol.setSub(MinicraftProtocol.Sub.HANDSHAKE, true, event.session);
            event.session.send(new HandshakePacket(
                    Constants.PROTOCOL_VERSION,
                    event.session.host(),
                    event.session.port(),
                    MinicraftProtocol.HandshakeIntent.STATUS));
            protocol.setSub(MinicraftProtocol.Sub.STATUS, true, event.session);
            event.session.send(new StatusQueryPacket());
        }
    }
}
