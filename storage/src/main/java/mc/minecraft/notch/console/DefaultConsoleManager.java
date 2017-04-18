package mc.minecraft.client.console;

import com.google.common.collect.EvictingQueue;
import mc.minecraft.data.message.ChatColor;
import mc.minecraft.data.message.Message;
import mc.minecraft.data.message.MessageStyle;
import mc.minecraft.data.message.TextMessage;

import java.util.stream.Stream;

import static mc.minecraft.data.message.ChatColor.DARK_GRAY;

public final class DefaultConsoleManager implements ConsoleManager {

    private final EvictingQueue<Message> messages = EvictingQueue.create(100);

    @Override
    public void send(Message message) {
        messages.add(message);
    }

    @Override
    public void send(Type type, String text) {
        MessageStyle style = new MessageStyle();
        final String prefix;
        switch (type) {
            case NOTIFY:
                style = style.setColor(ChatColor.DARK_AQUA);
                prefix = "NOTIFY";
                break;
            case SYSTEM:
                style = style.setColor(ChatColor.YELLOW);
                prefix = "SYSTEM";
                break;
            default:
                prefix = "MESSAGE";
                break;
        }
        Message message = new TextMessage(String.format("[%s] ", prefix));
        message.setStyle(style);
        message.addExtra(new TextMessage(text).setStyle(new MessageStyle().setColor(DARK_GRAY)));
        send(message);
    }

    @Override
    public int countMessages() {
        return messages.size();
    }

    @Override
    public Stream<Message> stream() {
        return messages.stream();
    }
}
