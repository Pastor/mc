package mc.minicraft.packet.ingame.server;

import mc.api.Buffer;
import mc.api.Packet;
import mc.minicraft.data.game.TitleAction;
import mc.minicraft.data.message.Message;

import java.io.IOException;

public class ServerTitlePacket implements Packet {
    private TitleAction action;

    private Message title;

    private Message subtitle;

    private Message actionBar;

    private int fadeIn;
    private int stay;
    private int fadeOut;

    @SuppressWarnings("unused")
    private ServerTitlePacket() {
    }

    public ServerTitlePacket(String title, boolean sub) {
        this(Message.fromString(title), sub);
    }

    public ServerTitlePacket(Message title, boolean sub) {
        this(sub ? TitleAction.SUBTITLE : TitleAction.TITLE, title);
    }

    public ServerTitlePacket(TitleAction action, String title) {
        this(action, Message.fromString(title));
    }

    public ServerTitlePacket(TitleAction action, Message title) {
        this.action = action;

        switch (action) {
            case TITLE:
                this.title = title;
                break;
            case SUBTITLE:
                this.subtitle = title;
                break;
            case ACTION_BAR:
                this.actionBar = title;
                break;
            default:
                throw new IllegalArgumentException("action must be one of TITLE, SUBTITLE, ACTION_BAR");
        }
    }

    public ServerTitlePacket(int fadeIn, int stay, int fadeOut) {
        this.action = TitleAction.TIMES;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public ServerTitlePacket(boolean clear) {
        if (clear) {
            this.action = TitleAction.CLEAR;
        } else {
            this.action = TitleAction.RESET;
        }
    }

    public TitleAction getAction() {
        return this.action;
    }

    public Message getTitle() {
        return this.title;
    }

    public Message getSubtitle() {
        return this.subtitle;
    }

    public Message getActionBar() {
        return this.actionBar;
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public int getStay() {
        return this.stay;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.action = mc.minicraft.Magic.key(TitleAction.class, in.readVarInt());
        switch (this.action) {
            case TITLE:
                this.title = Message.fromString(in.readString());
                break;
            case SUBTITLE:
                this.subtitle = Message.fromString(in.readString());
                break;
            case ACTION_BAR:
                this.actionBar = Message.fromString(in.readString());
                break;
            case TIMES:
                this.fadeIn = in.readInt();
                this.stay = in.readInt();
                this.fadeOut = in.readInt();
                break;
            case CLEAR:
                break;
            case RESET:
                break;
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeVarInt(mc.minicraft.Magic.value(Integer.class, this.action));
        switch (this.action) {
            case TITLE:
                out.writeString(this.title.toJsonString());
                break;
            case SUBTITLE:
                out.writeString(this.subtitle.toJsonString());
                break;
            case ACTION_BAR:
                out.writeString(this.actionBar.toJsonString());
                break;
            case TIMES:
                out.writeInt(this.fadeIn);
                out.writeInt(this.stay);
                out.writeInt(this.fadeOut);
                break;
            case CLEAR:
                break;
            case RESET:
                break;
        }
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return mc.minicraft.Util.toString(this);
    }
}
