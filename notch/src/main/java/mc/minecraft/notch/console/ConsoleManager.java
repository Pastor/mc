package mc.minecraft.notch.console;

import mc.minicraft.data.message.Message;

import java.util.stream.Stream;

public interface ConsoleManager {

    void send(Message message);

    void send(Type type, String message);

    int countMessages();

    Stream<Message> stream();

    enum Type {
        SYSTEM,
        NOTIFY,
        MESSAGE
    }
}
