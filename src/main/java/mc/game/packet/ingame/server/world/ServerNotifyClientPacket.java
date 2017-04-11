package mc.game.packet.ingame.server.world;

import mc.api.Buffer;
import mc.api.Packet;
import mc.game.data.game.entity.player.GameMode;
import mc.game.data.game.world.notify.*;

import java.io.IOException;

public class ServerNotifyClientPacket implements Packet {

    private ClientNotification notification;
    private ClientNotificationValue value;

    @SuppressWarnings("unused")
    private ServerNotifyClientPacket() {
    }

    public ServerNotifyClientPacket(ClientNotification notification, ClientNotificationValue value) {
        this.notification = notification;
        this.value = value;
    }

    public ClientNotification getNotification() {
        return this.notification;
    }

    public ClientNotificationValue getValue() {
        return this.value;
    }

    @Override
    public void read(Buffer.Input in) throws IOException {
        this.notification = mc.game.Magic.key(ClientNotification.class, in.readUnsignedByte());
        float value = in.readFloat();
        if (this.notification == ClientNotification.CHANGE_GAMEMODE) {
            this.value = mc.game.Magic.key(GameMode.class, (int) value);
        } else if (this.notification == ClientNotification.DEMO_MESSAGE) {
            this.value = mc.game.Magic.key(DemoMessageValue.class, (int) value);
        } else if (this.notification == ClientNotification.ENTER_CREDITS) {
            this.value = mc.game.Magic.key(EnterCreditsValue.class, (int) value);
        } else if (this.notification == ClientNotification.RAIN_STRENGTH) {
            this.value = new RainStrengthValue(value);
        } else if (this.notification == ClientNotification.THUNDER_STRENGTH) {
            this.value = new ThunderStrengthValue(value);
        }
    }

    @Override
    public void write(Buffer.Output out) throws IOException {
        out.writeByte(mc.game.Magic.value(Integer.class, this.notification));
        float value = 0;
        if (this.value instanceof Enum<?>) {
            value = mc.game.Magic.value(Integer.class, (Enum<?>) this.value);
        } else if (this.value instanceof RainStrengthValue) {
            value = ((RainStrengthValue) this.value).getStrength();
        } else if (this.value instanceof ThunderStrengthValue) {
            value = ((ThunderStrengthValue) this.value).getStrength();
        }

        out.writeFloat(value);
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
