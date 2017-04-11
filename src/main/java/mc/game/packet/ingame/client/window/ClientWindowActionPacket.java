package mc.game.packet.ingame.client.window;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.metadata.ItemStack;
import mc.game.data.game.window.*;

import java.io.IOException;

public class ClientWindowActionPacket implements Packet {

    private int windowId;
    private int slot;
    private WindowActionParam param;
    private int actionId;
    private WindowAction action;
    private ItemStack clicked;

    @SuppressWarnings("unused")
    private ClientWindowActionPacket() {
    }

    public ClientWindowActionPacket(int windowId, int actionId, int slot, ItemStack clicked, WindowAction action, WindowActionParam param) {
        this.windowId = windowId;
        this.actionId = actionId;
        this.slot = slot;
        this.clicked = clicked;
        this.action = action;
        this.param = param;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getActionId() {
        return this.actionId;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getClickedItem() {
        return this.clicked;
    }

    public WindowAction getAction() {
        return this.action;
    }

    public WindowActionParam getParam() {
        return this.param;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.windowId = in.readByte();
        this.slot = in.readShort();
        byte param = in.readByte();
        this.actionId = in.readShort();
        this.action = mc.game.Magic.key(WindowAction.class, in.readByte());
        this.clicked = mc.game.Util.readItem(in);
        if(this.action == WindowAction.CLICK_ITEM) {
            this.param = mc.game.Magic.key(ClickItemParam.class, param);
        } else if(this.action == WindowAction.SHIFT_CLICK_ITEM) {
            this.param = mc.game.Magic.key(ShiftClickItemParam.class, param);
        } else if(this.action == WindowAction.MOVE_TO_HOTBAR_SLOT) {
            this.param = mc.game.Magic.key(MoveToHotbarParam.class, param);
        } else if(this.action == WindowAction.CREATIVE_GRAB_MAX_STACK) {
            this.param = mc.game.Magic.key(CreativeGrabParam.class, param);
        } else if(this.action == WindowAction.DROP_ITEM) {
            this.param = mc.game.Magic.key(DropItemParam.class, param + (this.slot != -999 ? 2 : 0));
        } else if(this.action == WindowAction.SPREAD_ITEM) {
            this.param = mc.game.Magic.key(SpreadItemParam.class, param);
        } else if(this.action == WindowAction.FILL_STACK) {
            this.param = mc.game.Magic.key(FillStackParam.class, param);
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(this.windowId);
        out.writeShort(this.slot);
        int param = 0;
        if(this.action == WindowAction.CLICK_ITEM) {
            param = mc.game.Magic.value(Integer.class, (Enum<?>) this.param);
        } else if(this.action == WindowAction.SHIFT_CLICK_ITEM) {
            param = mc.game.Magic.value(Integer.class, (Enum<?>) this.param);
        } else if(this.action == WindowAction.MOVE_TO_HOTBAR_SLOT) {
            param = mc.game.Magic.value(Integer.class, (Enum<?>) this.param);
        } else if(this.action == WindowAction.CREATIVE_GRAB_MAX_STACK) {
            param = mc.game.Magic.value(Integer.class, (Enum<?>) this.param);
        } else if(this.action == WindowAction.DROP_ITEM) {
            param = mc.game.Magic.value(Integer.class, (Enum<?>) this.param) + (this.slot != -999 ? 2 : 0);
        } else if(this.action == WindowAction.SPREAD_ITEM) {
            param = mc.game.Magic.value(Integer.class, (Enum<?>) this.param);
        } else if(this.action == WindowAction.FILL_STACK) {
            param = mc.game.Magic.value(Integer.class, (Enum<?>) this.param);
        }

        out.writeByte(param);
        out.writeShort(this.actionId);
        out.writeByte(mc.game.Magic.value(Integer.class, this.action));
        mc.game.Util.writeItem(out, this.clicked);
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
