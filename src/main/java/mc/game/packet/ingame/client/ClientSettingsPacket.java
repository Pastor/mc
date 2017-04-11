package mc.game.packet.ingame.client;


import mc.api.Buffer;
import mc.api.Packet;
import mc.game.Magic;
import mc.game.data.game.entity.player.Hand;
import mc.game.data.game.setting.ChatVisibility;
import mc.game.data.game.setting.SkinPart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientSettingsPacket implements Packet {

    private String locale;
    private int renderDistance;
    private ChatVisibility chatVisibility;
    private boolean chatColors;
    private List<SkinPart> visibleParts;
    private Hand mainHand;

    @SuppressWarnings("unused")
    private ClientSettingsPacket() {
    }

    public ClientSettingsPacket(String locale, int renderDistance, ChatVisibility chatVisibility, boolean chatColors, SkinPart[] visibleParts, Hand mainHand) {
        this.locale = locale;
        this.renderDistance = renderDistance;
        this.chatVisibility = chatVisibility;
        this.chatColors = chatColors;
        this.visibleParts = Arrays.asList(visibleParts);
        this.mainHand = mainHand;
    }

    public String getLocale() {
        return this.locale;
    }

    public int getRenderDistance() {
        return this.renderDistance;
    }

    public ChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }

    public boolean getUseChatColors() {
        return this.chatColors;
    }

    public List<SkinPart> getVisibleParts() {
        return this.visibleParts;
    }

    public Hand getMainHand() {
        return this.mainHand;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.locale = in.readString();
        this.renderDistance = in.readByte();
        this.chatVisibility = Magic.key(ChatVisibility.class, in.readVarInt());
        this.chatColors = in.readBoolean();
        this.visibleParts = new ArrayList<SkinPart>();

        int flags = in.readUnsignedByte();
        for (SkinPart part : SkinPart.values()) {
            int bit = 1 << part.ordinal();
            if ((flags & bit) == bit) {
                this.visibleParts.add(part);
            }
        }

        this.mainHand = Magic.key(Hand.class, in.readVarInt());
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeString(this.locale);
        out.writeByte(this.renderDistance);
        out.writeVarInt(Magic.value(Integer.class, this.chatVisibility));
        out.writeBoolean(this.chatColors);

        int flags = 0;
        for (SkinPart part : this.visibleParts) {
            flags |= 1 << part.ordinal();
        }

        out.writeByte(flags);

        out.writeVarInt(Magic.value(Integer.class, this.mainHand));
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
