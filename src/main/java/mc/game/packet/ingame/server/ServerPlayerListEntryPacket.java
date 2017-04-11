package mc.game.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.Profile;
import mc.game.data.game.PlayerListEntry;
import mc.game.data.game.PlayerListEntryAction;
import mc.game.data.game.entity.player.GameMode;
import mc.game.data.message.Message;

import java.io.IOException;
import java.util.UUID;

public class ServerPlayerListEntryPacket implements Packet {
    private PlayerListEntryAction action;
    private PlayerListEntry entries[];

    @SuppressWarnings("unused")
    private ServerPlayerListEntryPacket() {
    }

    public ServerPlayerListEntryPacket(PlayerListEntryAction action, PlayerListEntry entries[]) {
        this.action = action;
        this.entries = entries;
    }

    public PlayerListEntryAction getAction() {
        return this.action;
    }

    public PlayerListEntry[] getEntries() {
        return this.entries;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.action = mc.game.Magic.key(PlayerListEntryAction.class, in.readVarInt());
        this.entries = new PlayerListEntry[in.readVarInt()];
        for (int count = 0; count < this.entries.length; count++) {
            UUID uuid = in.readUUID();
            Profile profile;
            if (this.action == PlayerListEntryAction.ADD_PLAYER) {
                profile = new Profile(uuid, in.readString());
            } else {
                profile = new Profile(uuid, null);
            }

            PlayerListEntry entry = null;
            switch (this.action) {
                case ADD_PLAYER:
                    int properties = in.readVarInt();
                    for (int index = 0; index < properties; index++) {
                        String propertyName = in.readString();
                        String value = in.readString();
                        String signature = null;
                        if (in.readBoolean()) {
                            signature = in.readString();
                        }

                        profile.properties().add(new Profile.Property(propertyName, value, signature));
                    }

                    int g = in.readVarInt();
                    GameMode gameMode = mc.game.Magic.key(GameMode.class, g < 0 ? 0 : g);
                    int ping = in.readVarInt();
                    Message displayName = null;
                    if (in.readBoolean()) {
                        displayName = Message.fromString(in.readString());
                    }

                    entry = new PlayerListEntry(profile, gameMode, ping, displayName);
                    break;
                case UPDATE_GAMEMODE:
                    g = in.readVarInt();
                    GameMode mode = mc.game.Magic.key(GameMode.class, g < 0 ? 0 : g);
                    entry = new PlayerListEntry(profile, mode);
                    break;
                case UPDATE_LATENCY:
                    int png = in.readVarInt();
                    entry = new PlayerListEntry(profile, png);
                    break;
                case UPDATE_DISPLAY_NAME:
                    Message disp = null;
                    if (in.readBoolean()) {
                        disp = Message.fromString(in.readString());
                    }

                    entry = new PlayerListEntry(profile, disp);
                case REMOVE_PLAYER:
                    entry = new PlayerListEntry(profile);
                    break;
            }

            this.entries[count] = entry;
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(mc.game.Magic.value(Integer.class, this.action));
        out.writeVarInt(this.entries.length);
        for (PlayerListEntry entry : this.entries) {
            out.writeUUID(entry.getProfile().uuid());
            switch (this.action) {
                case ADD_PLAYER:
                    out.writeString(entry.getProfile().name);
                    out.writeVarInt(entry.getProfile().properties().size());
                    for (Profile.Property property : entry.getProfile().properties()) {
                        out.writeString(property.name);
                        out.writeString(property.value);
                        out.writeBoolean(property.hasSignature());
                        if (property.hasSignature()) {
                            out.writeString(property.signature);
                        }
                    }

                    out.writeVarInt(mc.game.Magic.value(Integer.class, entry.getGameMode()));
                    out.writeVarInt(entry.getPing());
                    out.writeBoolean(entry.getDisplayName() != null);
                    if (entry.getDisplayName() != null) {
                        out.writeString(entry.getDisplayName().toJsonString());
                    }

                    break;
                case UPDATE_GAMEMODE:
                    out.writeVarInt(mc.game.Magic.value(Integer.class, entry.getGameMode()));
                    break;
                case UPDATE_LATENCY:
                    out.writeVarInt(entry.getPing());
                    break;
                case UPDATE_DISPLAY_NAME:
                    out.writeBoolean(entry.getDisplayName() != null);
                    if (entry.getDisplayName() != null) {
                        out.writeString(entry.getDisplayName().toJsonString());
                    }

                    break;
                case REMOVE_PLAYER:
                    break;
            }
        }
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.game.Util.toString(this);
    }
}
