package mc.minicraft;

import mc.api.*;
import mc.engine.DefaultPacket;
import mc.minicraft.packet.HandshakePacket;
import mc.minicraft.packet.ingame.client.*;
import mc.minicraft.packet.ingame.client.player.*;
import mc.minicraft.packet.ingame.client.window.*;
import mc.minicraft.packet.ingame.client.world.*;
import mc.minicraft.packet.ingame.server.*;
import mc.minicraft.packet.ingame.server.entity.*;
import mc.minicraft.packet.ingame.server.entity.player.*;
import mc.minicraft.packet.ingame.server.entity.spawn.*;
import mc.minicraft.packet.ingame.server.level.ServerStartLevelPacket;
import mc.minicraft.packet.ingame.server.level.ServerUpdateLevelPacket;
import mc.minicraft.packet.ingame.server.scoreboard.ServerDisplayScoreboardPacket;
import mc.minicraft.packet.ingame.server.scoreboard.ServerScoreboardObjectivePacket;
import mc.minicraft.packet.ingame.server.scoreboard.ServerTeamPacket;
import mc.minicraft.packet.ingame.server.scoreboard.ServerUpdateScorePacket;
import mc.minicraft.packet.ingame.server.window.*;
import mc.minicraft.packet.ingame.server.world.*;
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
        this.registerIncoming(0x00, ClientTeleportConfirmPacket.class);
        this.registerIncoming(0x01, ClientTabCompletePacket.class);
        this.registerIncoming(0x02, ClientChatPacket.class);
        this.registerIncoming(0x03, ClientRequestPacket.class);
        this.registerIncoming(0x04, ClientSettingsPacket.class);
        this.registerIncoming(0x05, ClientConfirmTransactionPacket.class);
        this.registerIncoming(0x06, ClientEnchantItemPacket.class);
        this.registerIncoming(0x07, ClientWindowActionPacket.class);
        this.registerIncoming(0x08, ClientCloseWindowPacket.class);
        this.registerIncoming(0x09, ClientPluginMessagePacket.class);
        this.registerIncoming(0x0A, ClientPlayerInteractEntityPacket.class);
        this.registerIncoming(0x0B, ClientKeepAlivePacket.class);
        this.registerIncoming(0x0C, ClientPlayerPositionPacket.class);
        this.registerIncoming(0x0D, ClientPlayerPositionRotationPacket.class);
        this.registerIncoming(0x0E, ClientPlayerRotationPacket.class);
        this.registerIncoming(0x0F, ClientPlayerMovementPacket.class);
        this.registerIncoming(0x10, ClientVehicleMovePacket.class);
        this.registerIncoming(0x11, ClientSteerBoatPacket.class);
        this.registerIncoming(0x12, ClientPlayerAbilitiesPacket.class);
        this.registerIncoming(0x13, ClientPlayerActionPacket.class);
        this.registerIncoming(0x14, ClientPlayerStatePacket.class);
        this.registerIncoming(0x15, ClientSteerVehiclePacket.class);
        this.registerIncoming(0x16, ClientResourcePackStatusPacket.class);
        this.registerIncoming(0x17, ClientPlayerChangeHeldItemPacket.class);
        this.registerIncoming(0x18, ClientCreativeInventoryActionPacket.class);
        this.registerIncoming(0x19, ClientUpdateSignPacket.class);
        this.registerIncoming(0x1A, ClientPlayerSwingArmPacket.class);
        this.registerIncoming(0x1B, ClientSpectatePacket.class);
        this.registerIncoming(0x1C, ClientPlayerPlaceBlockPacket.class);
        this.registerIncoming(0x1D, ClientPlayerUseItemPacket.class);

        this.registerOutgoing(0x00, ServerSpawnObjectPacket.class);
        this.registerOutgoing(0x01, ServerSpawnExpOrbPacket.class);
        this.registerOutgoing(0x02, ServerSpawnGlobalEntityPacket.class);
        this.registerOutgoing(0x03, ServerSpawnMobPacket.class);
        this.registerOutgoing(0x04, ServerSpawnPaintingPacket.class);
        this.registerOutgoing(0x05, ServerSpawnPlayerPacket.class);
        this.registerOutgoing(0x06, ServerEntityAnimationPacket.class);
        this.registerOutgoing(0x07, ServerStatisticsPacket.class);
        this.registerOutgoing(0x08, ServerBlockBreakAnimPacket.class);
        this.registerOutgoing(0x09, ServerUpdateTileEntityPacket.class);
        this.registerOutgoing(0x0A, ServerBlockValuePacket.class);
        this.registerOutgoing(0x0B, ServerBlockChangePacket.class);
        this.registerOutgoing(0x0C, ServerBossBarPacket.class);
        this.registerOutgoing(0x0D, ServerDifficultyPacket.class);
        this.registerOutgoing(0x0E, ServerTabCompletePacket.class);
        this.registerOutgoing(0x0F, ServerChatPacket.class);
        this.registerOutgoing(0x10, ServerMultiBlockChangePacket.class);
        this.registerOutgoing(0x11, ServerConfirmTransactionPacket.class);
        this.registerOutgoing(0x12, ServerCloseWindowPacket.class);
        this.registerOutgoing(0x13, ServerOpenWindowPacket.class);
        this.registerOutgoing(0x14, ServerWindowItemsPacket.class);
        this.registerOutgoing(0x15, ServerWindowPropertyPacket.class);
        this.registerOutgoing(0x16, ServerSetSlotPacket.class);
        this.registerOutgoing(0x17, ServerSetCooldownPacket.class);
        this.registerOutgoing(0x18, ServerPluginMessagePacket.class);
        this.registerOutgoing(0x19, ServerPlaySoundPacket.class);
        this.registerOutgoing(0x1A, ServerDisconnectPacket.class);
        this.registerOutgoing(0x1B, ServerEntityStatusPacket.class);
        this.registerOutgoing(0x1C, ServerExplosionPacket.class);
        this.registerOutgoing(0x1D, ServerUnloadChunkPacket.class);
        this.registerOutgoing(0x1E, ServerNotifyClientPacket.class);
        this.registerOutgoing(0x1F, ServerKeepAlivePacket.class);
        this.registerOutgoing(0x20, ServerChunkDataPacket.class);
        this.registerOutgoing(0x21, ServerPlayEffectPacket.class);
        this.registerOutgoing(0x22, ServerSpawnParticlePacket.class);
        this.registerOutgoing(0x23, ServerJoinGamePacket.class);
        this.registerOutgoing(0x24, ServerMapDataPacket.class);
        this.registerOutgoing(0x25, ServerEntityPositionPacket.class);
        this.registerOutgoing(0x26, ServerEntityPositionRotationPacket.class);
        this.registerOutgoing(0x27, ServerEntityRotationPacket.class);
        this.registerOutgoing(0x28, ServerEntityMovementPacket.class);
        this.registerOutgoing(0x29, ServerVehicleMovePacket.class);
        this.registerOutgoing(0x2A, ServerOpenTileEntityEditorPacket.class);
        this.registerOutgoing(0x2B, ServerPlayerAbilitiesPacket.class);
        this.registerOutgoing(0x2C, ServerCombatPacket.class);
        this.registerOutgoing(0x2D, ServerPlayerListEntryPacket.class);
        this.registerOutgoing(0x2E, ServerPlayerPositionRotationPacket.class);
        this.registerOutgoing(0x2F, ServerPlayerUseBedPacket.class);
        this.registerOutgoing(0x30, ServerEntityDestroyPacket.class);
        this.registerOutgoing(0x31, ServerEntityRemoveEffectPacket.class);
        this.registerOutgoing(0x32, ServerResourcePackSendPacket.class);
        this.registerOutgoing(0x33, ServerRespawnPacket.class);
        this.registerOutgoing(0x34, ServerEntityHeadLookPacket.class);
        this.registerOutgoing(0x35, ServerWorldBorderPacket.class);
        this.registerOutgoing(0x36, ServerSwitchCameraPacket.class);
        this.registerOutgoing(0x37, ServerPlayerChangeHeldItemPacket.class);
        this.registerOutgoing(0x38, ServerDisplayScoreboardPacket.class);
        this.registerOutgoing(0x39, ServerEntityMetadataPacket.class);
        this.registerOutgoing(0x3A, ServerEntityAttachPacket.class);
        this.registerOutgoing(0x3B, ServerEntityVelocityPacket.class);
        this.registerOutgoing(0x3C, ServerEntityEquipmentPacket.class);
        this.registerOutgoing(0x3D, ServerPlayerSetExperiencePacket.class);
        this.registerOutgoing(0x3E, ServerPlayerHealthPacket.class);
        this.registerOutgoing(0x3F, ServerScoreboardObjectivePacket.class);
        this.registerOutgoing(0x40, ServerEntitySetPassengersPacket.class);
        this.registerOutgoing(0x41, ServerTeamPacket.class);
        this.registerOutgoing(0x42, ServerUpdateScorePacket.class);
        this.registerOutgoing(0x43, ServerSpawnPositionPacket.class);
        this.registerOutgoing(0x44, ServerUpdateTimePacket.class);
        this.registerOutgoing(0x45, ServerTitlePacket.class);
        this.registerOutgoing(0x46, ServerPlayBuiltinSoundPacket.class);
        this.registerOutgoing(0x47, ServerPlayerListDataPacket.class);
        this.registerOutgoing(0x48, ServerEntityCollectItemPacket.class);
        this.registerOutgoing(0x49, ServerEntityTeleportPacket.class);
        this.registerOutgoing(0x4A, ServerEntityPropertiesPacket.class);
        this.registerOutgoing(0x4B, ServerEntityEffectPacket.class);

        {
            this.registerIncoming(0xFD, ClientPlayerSettings.class);
            this.registerIncoming(0xFB, ClientPlayerUpdatePacket.class);

            this.registerOutgoing(0xFC, ServerUpdateLevelPacket.class);
            this.registerOutgoing(0xFE, ServerStartLevelPacket.class);
        }
    }

    private void initClientGame(Session session) {
        this.registerIncoming(0x00, ServerSpawnObjectPacket.class);
        this.registerIncoming(0x01, ServerSpawnExpOrbPacket.class);
        this.registerIncoming(0x02, ServerSpawnGlobalEntityPacket.class);
        this.registerIncoming(0x03, ServerSpawnMobPacket.class);
        this.registerIncoming(0x04, ServerSpawnPaintingPacket.class);
        this.registerIncoming(0x05, ServerSpawnPlayerPacket.class);
        this.registerIncoming(0x06, ServerEntityAnimationPacket.class);
        this.registerIncoming(0x07, ServerStatisticsPacket.class);
        this.registerIncoming(0x08, ServerBlockBreakAnimPacket.class);
        this.registerIncoming(0x09, ServerUpdateTileEntityPacket.class);
        this.registerIncoming(0x0A, ServerBlockValuePacket.class);
        this.registerIncoming(0x0B, ServerBlockChangePacket.class);
        this.registerIncoming(0x0C, ServerBossBarPacket.class);
        this.registerIncoming(0x0D, ServerDifficultyPacket.class);
        this.registerIncoming(0x0E, ServerTabCompletePacket.class);
        this.registerIncoming(0x0F, ServerChatPacket.class);
        this.registerIncoming(0x10, ServerMultiBlockChangePacket.class);
        this.registerIncoming(0x11, ServerConfirmTransactionPacket.class);
        this.registerIncoming(0x12, ServerCloseWindowPacket.class);
        this.registerIncoming(0x13, ServerOpenWindowPacket.class);
        this.registerIncoming(0x14, ServerWindowItemsPacket.class);
        this.registerIncoming(0x15, ServerWindowPropertyPacket.class);
        this.registerIncoming(0x16, ServerSetSlotPacket.class);
        this.registerIncoming(0x17, ServerSetCooldownPacket.class);
        this.registerIncoming(0x18, ServerPluginMessagePacket.class);
        this.registerIncoming(0x19, ServerPlaySoundPacket.class);
        this.registerIncoming(0x1A, ServerDisconnectPacket.class);
        this.registerIncoming(0x1B, ServerEntityStatusPacket.class);
        this.registerIncoming(0x1C, ServerExplosionPacket.class);
        this.registerIncoming(0x1D, ServerUnloadChunkPacket.class);
        this.registerIncoming(0x1E, ServerNotifyClientPacket.class);
        this.registerIncoming(0x1F, ServerKeepAlivePacket.class);
        this.registerIncoming(0x20, ServerChunkDataPacket.class);
        this.registerIncoming(0x21, ServerPlayEffectPacket.class);
        this.registerIncoming(0x22, ServerSpawnParticlePacket.class);
        this.registerIncoming(0x23, ServerJoinGamePacket.class);
        this.registerIncoming(0x24, ServerMapDataPacket.class);
        this.registerIncoming(0x25, ServerEntityPositionPacket.class);
        this.registerIncoming(0x26, ServerEntityPositionRotationPacket.class);
        this.registerIncoming(0x27, ServerEntityRotationPacket.class);
        this.registerIncoming(0x28, ServerEntityMovementPacket.class);
        this.registerIncoming(0x29, ServerVehicleMovePacket.class);
        this.registerIncoming(0x2A, ServerOpenTileEntityEditorPacket.class);
        this.registerIncoming(0x2B, ServerPlayerAbilitiesPacket.class);
        this.registerIncoming(0x2C, ServerCombatPacket.class);
        this.registerIncoming(0x2D, ServerPlayerListEntryPacket.class);
        this.registerIncoming(0x2E, ServerPlayerPositionRotationPacket.class);
        this.registerIncoming(0x2F, ServerPlayerUseBedPacket.class);
        this.registerIncoming(0x30, ServerEntityDestroyPacket.class);
        this.registerIncoming(0x31, ServerEntityRemoveEffectPacket.class);
        this.registerIncoming(0x32, ServerResourcePackSendPacket.class);
        this.registerIncoming(0x33, ServerRespawnPacket.class);
        this.registerIncoming(0x34, ServerEntityHeadLookPacket.class);
        this.registerIncoming(0x35, ServerWorldBorderPacket.class);
        this.registerIncoming(0x36, ServerSwitchCameraPacket.class);
        this.registerIncoming(0x37, ServerPlayerChangeHeldItemPacket.class);
        this.registerIncoming(0x38, ServerDisplayScoreboardPacket.class);
        this.registerIncoming(0x39, ServerEntityMetadataPacket.class);
        this.registerIncoming(0x3A, ServerEntityAttachPacket.class);
        this.registerIncoming(0x3B, ServerEntityVelocityPacket.class);
        this.registerIncoming(0x3C, ServerEntityEquipmentPacket.class);
        this.registerIncoming(0x3D, ServerPlayerSetExperiencePacket.class);
        this.registerIncoming(0x3E, ServerPlayerHealthPacket.class);
        this.registerIncoming(0x3F, ServerScoreboardObjectivePacket.class);
        this.registerIncoming(0x40, ServerEntitySetPassengersPacket.class);
        this.registerIncoming(0x41, ServerTeamPacket.class);
        this.registerIncoming(0x42, ServerUpdateScorePacket.class);
        this.registerIncoming(0x43, ServerSpawnPositionPacket.class);
        this.registerIncoming(0x44, ServerUpdateTimePacket.class);
        this.registerIncoming(0x45, ServerTitlePacket.class);
        this.registerIncoming(0x46, ServerPlayBuiltinSoundPacket.class);
        this.registerIncoming(0x47, ServerPlayerListDataPacket.class);
        this.registerIncoming(0x48, ServerEntityCollectItemPacket.class);
        this.registerIncoming(0x49, ServerEntityTeleportPacket.class);
        this.registerIncoming(0x4A, ServerEntityPropertiesPacket.class);
        this.registerIncoming(0x4B, ServerEntityEffectPacket.class);

        {
            this.registerOutgoing(0xFD, ClientPlayerSettings.class);
            this.registerOutgoing(0xFB, ClientPlayerUpdatePacket.class);

            this.registerIncoming(0xFE, ServerStartLevelPacket.class);
            this.registerIncoming(0xFC, ServerUpdateLevelPacket.class);
        }

        this.registerOutgoing(0x00, ClientTeleportConfirmPacket.class);
        this.registerOutgoing(0x01, ClientTabCompletePacket.class);
        this.registerOutgoing(0x02, ClientChatPacket.class);
        this.registerOutgoing(0x03, ClientRequestPacket.class);
        this.registerOutgoing(0x04, ClientSettingsPacket.class);
        this.registerOutgoing(0x05, ClientConfirmTransactionPacket.class);
        this.registerOutgoing(0x06, ClientEnchantItemPacket.class);
        this.registerOutgoing(0x07, ClientWindowActionPacket.class);
        this.registerOutgoing(0x08, ClientCloseWindowPacket.class);
        this.registerOutgoing(0x09, ClientPluginMessagePacket.class);
        this.registerOutgoing(0x0A, ClientPlayerInteractEntityPacket.class);
        this.registerOutgoing(0x0B, ClientKeepAlivePacket.class);
        this.registerOutgoing(0x0C, ClientPlayerPositionPacket.class);
        this.registerOutgoing(0x0D, ClientPlayerPositionRotationPacket.class);
        this.registerOutgoing(0x0E, ClientPlayerRotationPacket.class);
        this.registerOutgoing(0x0F, ClientPlayerMovementPacket.class);
        this.registerOutgoing(0x10, ClientVehicleMovePacket.class);
        this.registerOutgoing(0x11, ClientSteerBoatPacket.class);
        this.registerOutgoing(0x12, ClientPlayerAbilitiesPacket.class);
        this.registerOutgoing(0x13, ClientPlayerActionPacket.class);
        this.registerOutgoing(0x14, ClientPlayerStatePacket.class);
        this.registerOutgoing(0x15, ClientSteerVehiclePacket.class);
        this.registerOutgoing(0x16, ClientResourcePackStatusPacket.class);
        this.registerOutgoing(0x17, ClientPlayerChangeHeldItemPacket.class);
        this.registerOutgoing(0x18, ClientCreativeInventoryActionPacket.class);
        this.registerOutgoing(0x19, ClientUpdateSignPacket.class);
        this.registerOutgoing(0x1A, ClientPlayerSwingArmPacket.class);
        this.registerOutgoing(0x1B, ClientSpectatePacket.class);
        this.registerOutgoing(0x1C, ClientPlayerPlaceBlockPacket.class);
        this.registerOutgoing(0x1D, ClientPlayerUseItemPacket.class);
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
