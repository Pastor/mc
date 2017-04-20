package mc.minecraft.client.console;

import mc.api.Session;
import mc.minecraft.data.message.ChatColor;
import mc.minecraft.data.message.Message;
import mc.minecraft.data.message.MessageStyle;
import mc.minecraft.data.message.TextMessage;
import mc.minicraft.engine.gfx.Color;
import mc.minicraft.engine.gfx.Font;
import mc.minicraft.engine.gfx.Screen;
import mc.minecraft.client.sound.Sound;
import mc.minecraft.packet.ingame.client.ClientChatPacket;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public final class ConsoleMenu extends mc.minecraft.client.screen.Menu {
    private static final String MAINE_KEY = "Я";
    private final mc.minecraft.client.screen.Menu parent;

    private final List<String> history = new LinkedList<>();
    private final StringBuilder buffer = new StringBuilder();
    private Session session;
    private int tickCount;
    private int historySelected;

    public ConsoleMenu(mc.minecraft.client.screen.Menu parent) {
        super(parent.propertyReader);
        this.parent = parent;
    }

    public void tick() {
        if (input.escape.clicked) {
            game.setMenu(parent);
        } else if (input.down.clicked && !input.character.clicked) {
            --historySelected;
            if (historySelected < 0)
                historySelected = 0;
            updateHistoryBuffer();
        } else if (input.up.clicked && !input.character.clicked) {
            ++historySelected;
            if (historySelected >= history.size())
                historySelected = history.size() - 1;
            if (historySelected < 0)
                historySelected = 0;
            updateHistoryBuffer();
        } else if (input.enter.clicked) {
            history.add(buffer.toString());
            if (buffer.length() > 0) {
                Message message = null;
                if (buffer.charAt(0) == '/') {
                    StringTokenizer tokenizer = new StringTokenizer(buffer.toString(), " ", false);
                    String command = null;
                    List<String> arguments = new LinkedList<>();
                    while (tokenizer.hasMoreElements()) {
                        String element = tokenizer.nextToken();
                        if (message == null) {
                            command = element.substring(1);
                            message = new TextMessage(element).setStyle(new MessageStyle().setColor(ChatColor.LIGHT_YELLOW));
                        } else {
                            arguments.add(element);
                            message.addExtra(new TextMessage(" " + element).setStyle(
                                    new MessageStyle().setColor(ChatColor.GRAY)));
                        }
                    }
                    if (command != null) {
                        CommandProcessor.Result result = game.commandProcessor().execute(command, arguments);
                        message.addExtra(new TextMessage("-"));
                        message.addExtra(new TextMessage(result.result).setStyle(
                                new MessageStyle().setColor(
                                        result.isSuccess ? ChatColor.GREEN : ChatColor.RED
                                )
                        ));
                    }
                } else {
                    message = new TextMessage("[" + MAINE_KEY + "]")
                            .setStyle(new MessageStyle().setColor(ChatColor.DARK_RED));
                    message.addExtra(new TextMessage(buffer.toString()));
                    if (session != null) {
                        session.send(new ClientChatPacket(buffer.toString()));
                    }
                }
                if (message != null)
                    game.consoleManager().send(message);
                buffer.setLength(0);
            }
        } else if (input.backspace.clicked && buffer.length() > 0) {
            buffer.setLength(buffer.length() - 1);
        } else if (input.character.hasCharacter()) {
            if (((buffer.toString().length() + 1 + 3 + MAINE_KEY.length()) * 8) + 32 >= game.width()) {
                Sound.test.play();
            } else {
                buffer.append(input.character.ch);
            }
        }
        ++tickCount;
    }

    private void updateHistoryBuffer() {
        buffer.setLength(0);
        if (history.size() > historySelected)
            buffer.append(history.get(historySelected));
    }

    public void render(Screen screen) {
        screen.clear(0);
        renderMessages(screen);
        int xx = 8;
        int yy = (game.height() - 2 * 8);
        int inputLength = buffer.toString().length() + 1;
        int w = inputLength + 1;
        int h = 1;
        int row = 0;

        screen.render(xx - 8, yy - 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 0);
        screen.render(xx + w * 8, yy - 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 1);
        screen.render(xx - 8, yy + 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 2);
        screen.render(xx + w * 8, yy + 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 3);
        ++row;
        for (int x = 0; x < w; x++) {
            screen.render(xx + x * 8, yy - 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 0);
            screen.render(xx + x * 8, yy + 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 2);
        }
        ++row;
        for (int y = 0; y < h; y++) {
            screen.render(xx - 8, yy + y * 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 0);
            screen.render(xx + w * 8, yy + y * 8, row + 13 * 32, Color.get(-1, 1, 5, 445), 1);
        }
        int col = Color.get(5, 555, 555, 555);
        Font.draw(">", screen, xx, yy, col);
        if (buffer.toString().startsWith("/"))
            col = Color.get(5, 440, 440, 440);
        Font.draw(buffer.toString(), screen, xx + 8, yy, col);
        if ((tickCount / 20) % 2 == 0) {
            screen.render(xx + (inputLength * 8), yy, 28 * 32, Color.get(5, 333, 333, 333), 0);
        } else {
            screen.render(xx + (inputLength * 8), yy, 28 * 32, col, 0);
        }
    }

    private boolean moreMessages() {
        return game.consoleManager().countMessages() > maxMessages();
    }

    private int maxMessages() {
        return (game.height() - 8) / 8;
    }

    private void renderMessages(Screen screen) {
        ConsoleManager manager = game.consoleManager();
        if (manager.countMessages() > 0) {
            int x = 0;
            int y = 0;

            final List<Message> view;
            if (moreMessages()) {
                int index = manager.countMessages() - maxMessages();
                view = manager.stream().skip(index).limit(manager.countMessages() - index).collect(Collectors.toList());
            } else {
                view = manager.stream().collect(Collectors.toList());
            }

            for (Message message : view) {
                renderMessage(screen, x, y, message);
                x = 0;
                y += 8;
            }
        }
    }

    private static Point renderMessage(Screen screen, int x, int y, Message message) {
        MessageStyle style = message.getStyle();
        String text = message.getText();
        int color = color(style);
        Font.draw(text, screen, x, y, color);
        x += (text.length() * 8);
        for (Message m : message.getExtra()) {
            Point point = renderMessage(screen, x, y, m);
            x = point.x;
            y = point.y;
        }
        return new Point(x, y);
    }

    private static int color(MessageStyle style) {
        switch (style.getColor()) {
            case WHITE:
                return Color.get(0, 555, 555, 555);
            case DARK_RED:
                return Color.get(-1, 500, 500, 500);
            case RED:
                return Color.get(-1, 500, 000, 500);
            case DARK_BLUE:
                return Color.get(-1, 005, 005, 005);
            case DARK_AQUA:
                return Color.get(-1, 45, 45, 45);
            case BLUE:
                return Color.get(-1, 005, 055, 005);
            case DARK_GREEN:
                return Color.get(-1, 050, 050, 050);
            case DARK_GRAY:
                return Color.get(-1, 333, 333, 333);
            case GRAY:
                return Color.get(-1, 333, 333, 333);
            case LIGHT_YELLOW:
                return Color.get(-1, 333, 333, 440);
            case YELLOW:
                return Color.get(-1, 500, 500, 550);
        }
        return Color.get(0, 555, 555, 555);
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
