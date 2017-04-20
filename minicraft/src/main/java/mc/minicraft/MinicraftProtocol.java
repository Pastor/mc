package mc.minicraft;

import mc.api.*;
import mc.engine.DefaultPacket;
import mc.minicraft.packet.HandshakePacket;
import mc.minicraft.packet.ingame.client.*;
import mc.minicraft.packet.ingame.client.player.ClientPlayerAttackPacket;
import mc.minicraft.packet.ingame.client.player.ClientPlayerPositionPacket;
import mc.minicraft.packet.ingame.client.player.ClientPlayerSettings;
import mc.minicraft.packet.ingame.client.player.ClientPlayerUpdatePacket;
import mc.minicraft.packet.ingame.server.*;
import mc.minicraft.packet.ingame.server.level.ServerChangeLevelPacket;
import mc.minicraft.packet.ingame.server.level.ServerStartLevelPacket;
import mc.minicraft.packet.ingame.server.level.ServerUpdateLevelPacket;
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

import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.UUID;

public final class MinicraftProtocol extends Protocol {

    private Sub sub = Sub.HANDSHAKE;
    private final Packet.Header header = new DefaultPacket.Header();
    private Packet.Encrypt encrypt;
    public Profile profile;
    private String accessToken;

    public MinicraftProtocol() {

    }

    public MinicraftProtocol(Sub sub) {
        if (sub != Sub.LOGIN && sub != Sub.STATUS) {
            throw new IllegalArgumentException("Only login and status modes are permitted.");
        }
        this.sub = sub;
        /*if (sub == Sub.LOGIN) {
            this.profile = new Profile((UUID) null, "Player");
        }*/
    }

    public MinicraftProtocol(String username) {
        this(Sub.LOGIN);
        //this.profile = new Profile((UUID) null, username);
    }

    public MinicraftProtocol(String username, String password) {
        this(username, password, false);
    }

    public MinicraftProtocol(String username, String passwordOrToken, boolean token) {
        this(username, passwordOrToken, token, Proxy.NO_PROXY);
    }

    private MinicraftProtocol(String username, String passwordOrToken, boolean token, Proxy authProxy) {
        this(Sub.LOGIN);
    }

    public MinicraftProtocol(Profile profile, String accessToken) {
        this(Sub.LOGIN);
        this.profile = profile;
        this.accessToken = accessToken;
    }

    @Override
    public boolean authorize(String username, String password) {
        String clientToken = UUID.randomUUID().toString();
        this.profile = new Profile(clientToken, username);
        /*AuthenticationService auth = new AuthenticationService(clientToken, authProxy);
        auth.setUsername(username);
        if (token) {
            auth.setAccessToken(passwordOrToken);
        } else {
            auth.setPassword(passwordOrToken);
        }

        auth.login();
        this.profile = auth.getSelectedProfile();
        this.accessToken = auth.getAccessToken();*/
        return isAuthorized();
    }

    @Override
    public boolean unauthorize() {
        profile = null;
        return true;
    }

    @Override
    public boolean isAuthorized() {
        return profile != null;
    }

    public String prefix() {
        return "_minicraft";
    }

    public Packet.Header header() {
        return header;
    }

    public Packet.Encrypt encrypt() {
        return encrypt;
    }

    MinicraftProtocol enableEncryption(Key key) {
        try {
            this.encrypt = new DefaultPacket.Encrypt(key);
        } catch (GeneralSecurityException e) {
            throw new Error("Failed to enable protocol encryption.", e);
        }
        return this;
    }

    public void newSession(Client client, Session session) {
        if (this.profile != null) {
            session.setFlag(Constants.PROFILE_KEY, this.profile);
            session.setFlag(Constants.ACCESS_TOKEN_KEY, this.accessToken);
        }

        this.setSub(this.sub, true, session);
        session.addListener(new ClientListener());
    }

    public void newSession(mc.api.Server server, Session session, PlayerManager manager) {
        this.setSub(Sub.HANDSHAKE, false, session);
        session.addListener(new ServerListener(manager));
    }

    MinicraftProtocol setSub(Sub sub, boolean client, Session session) {
        this.clear();
        switch (sub) {
            case HANDSHAKE:
                if (client) {
                    this.initClientHandshake(session);
                } else {
                    this.initServerHandshake(session);
                }
                break;
            case LOGIN:
                if (client) {
                    this.initClientLogin(session);
                } else {
                    this.initServerLogin(session);
                }
                break;
            case GAME:
                if (client) {
                    this.initClientGame(session);
                } else {
                    this.initServerGame(session);
                }
                break;
            case STATUS:
                if (client) {
                    this.initClientStatus(session);
                } else {
                    this.initServerStatus(session);
                }
                break;
        }
        this.sub = sub;
        return this;
    }

    private void initServerGame(Session session) {
        this.registerIncoming(0x02, ClientChatPacket.class);
        this.registerIncoming(0x03, ClientRequestPacket.class);
        this.registerIncoming(0x04, ClientSettingsPacket.class);
        this.registerIncoming(0x0B, ClientKeepAlivePacket.class);

        this.registerOutgoing(0x07, ServerStatisticsPacket.class);
        this.registerOutgoing(0x0F, ServerChatPacket.class);
        this.registerOutgoing(0x1A, ServerDisconnectPacket.class);
        this.registerOutgoing(0x1F, ServerKeepAlivePacket.class);
        this.registerOutgoing(0x23, ServerJoinGamePacket.class);
        this.registerOutgoing(0x2D, ServerPlayerListEntryPacket.class);
        this.registerOutgoing(0x32, ServerResourcePackSendPacket.class);
        this.registerOutgoing(0x33, ServerRespawnPacket.class);
        this.registerOutgoing(0x47, ServerPlayerListDataPacket.class);

        {
            this.registerIncoming(0xFD, ClientPlayerSettings.class);
            this.registerIncoming(0xFB, ClientPlayerPositionPacket.class);
            this.registerIncoming(0xFA, ClientPlayerAttackPacket.class);
            this.registerIncoming(0xF8, ClientPlayerUpdatePacket.class);
            this.registerIncoming(0xF5, ClientRespawnPacket.class);

            this.registerOutgoing(0xFC, ServerUpdateLevelPacket.class);
            this.registerOutgoing(0xFE, ServerStartLevelPacket.class);
            this.registerOutgoing(0xF9, ServerSoundEffectPacket.class);
            this.registerOutgoing(0xF7, ServerChangeLevelPacket.class);
            this.registerOutgoing(0xF6, ServerUpdateEntityPacket.class);
        }
    }

    private void initClientGame(Session session) {
        this.registerIncoming(0x07, ServerStatisticsPacket.class);
        this.registerIncoming(0x0F, ServerChatPacket.class);
        this.registerIncoming(0x1A, ServerDisconnectPacket.class);
        this.registerIncoming(0x1F, ServerKeepAlivePacket.class);
        this.registerIncoming(0x23, ServerJoinGamePacket.class);
        this.registerIncoming(0x2D, ServerPlayerListEntryPacket.class);
        this.registerIncoming(0x32, ServerResourcePackSendPacket.class);
        this.registerIncoming(0x33, ServerRespawnPacket.class);
        this.registerIncoming(0x47, ServerPlayerListDataPacket.class);

        {
            this.registerOutgoing(0xFD, ClientPlayerSettings.class);
            this.registerOutgoing(0xFB, ClientPlayerPositionPacket.class);
            this.registerOutgoing(0xFA, ClientPlayerAttackPacket.class);
            this.registerOutgoing(0xF8, ClientPlayerUpdatePacket.class);
            this.registerOutgoing(0xF5, ClientRespawnPacket.class);

            this.registerIncoming(0xFE, ServerStartLevelPacket.class);
            this.registerIncoming(0xFC, ServerUpdateLevelPacket.class);
            this.registerIncoming(0xF9, ServerSoundEffectPacket.class);
            this.registerIncoming(0xF7, ServerChangeLevelPacket.class);
            this.registerIncoming(0xF6, ServerUpdateEntityPacket.class);
        }

        this.registerOutgoing(0x02, ClientChatPacket.class);
        this.registerOutgoing(0x03, ClientRequestPacket.class);
        this.registerOutgoing(0x04, ClientSettingsPacket.class);
        this.registerOutgoing(0x0B, ClientKeepAlivePacket.class);
    }

    public Sub getSub() {
        return sub;
    }

    private void initClientHandshake(Session session) {
        this.registerOutgoing(0, HandshakePacket.class);
    }

    private void initServerHandshake(Session session) {
        this.registerIncoming(0, HandshakePacket.class);
    }

    private void initClientLogin(Session session) {
        this.registerIncoming(0x00, LoginDisconnectPacket.class);
        this.registerIncoming(0x01, EncryptionRequestPacket.class);
        this.registerIncoming(0x02, LoginSuccessPacket.class);
        this.registerIncoming(0x03, LoginSetCompressionPacket.class);

        this.registerOutgoing(0x00, LoginStartPacket.class);
        this.registerOutgoing(0x01, EncryptionResponsePacket.class);
    }

    private void initServerLogin(Session session) {
        this.registerIncoming(0x00, LoginStartPacket.class);
        this.registerIncoming(0x01, EncryptionResponsePacket.class);

        this.registerOutgoing(0x00, LoginDisconnectPacket.class);
        this.registerOutgoing(0x01, EncryptionRequestPacket.class);
        this.registerOutgoing(0x02, LoginSuccessPacket.class);
        this.registerOutgoing(0x03, LoginSetCompressionPacket.class);
    }

    private void initClientStatus(Session session) {
        this.registerIncoming(0x00, StatusResponsePacket.class);
        this.registerIncoming(0x01, StatusPongPacket.class);

        this.registerOutgoing(0x00, StatusQueryPacket.class);
        this.registerOutgoing(0x01, StatusPingPacket.class);
    }

    private void initServerStatus(Session session) {
        this.registerIncoming(0x00, StatusQueryPacket.class);
        this.registerIncoming(0x01, StatusPingPacket.class);

        this.registerOutgoing(0x00, StatusResponsePacket.class);
        this.registerOutgoing(0x01, StatusPongPacket.class);
    }

    public enum Sub {
        HANDSHAKE,
        LOGIN,
        GAME,
        STATUS
    }

    public enum HandshakeIntent {
        STATUS,
        LOGIN
    }
}
