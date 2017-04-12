package mc.minecraft.notch.screen;

import com.google.common.collect.EvictingQueue;
import mc.api.Session;
import mc.minecraft.data.message.ChatColor;
import mc.minecraft.data.message.Message;
import mc.minecraft.data.message.MessageStyle;
import mc.minecraft.data.message.TextMessage;
import mc.minecraft.notch.Game;
import mc.minecraft.notch.gfx.Color;
import mc.minecraft.notch.gfx.Font;
import mc.minecraft.notch.gfx.Screen;
import mc.minecraft.notch.sound.Sound;
import mc.minecraft.packet.ingame.client.ClientChatPacket;

import java.awt.*;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public final class ConsoleMenu extends Menu {
    private static final String MAINE_KEY = "Я";
    private final Menu parent;
    private final EvictingQueue<Message> messages = EvictingQueue.create(100);
    private final StringBuilder buffer = new StringBuilder();
    private Session session;
    private int tickCount;

    ConsoleMenu(Menu parent) {
        this.parent = parent;
    }

    public void tick() {
        if (input.escape.clicked) {
            game.setMenu(parent);
        } else if (input.enter.clicked) {
            if (buffer.length() > 0) {
                Message message = null;
                if (buffer.charAt(0) == '\\') {
                    StringTokenizer tokenizer = new StringTokenizer(buffer.toString(), " ", false);
                    while (tokenizer.hasMoreElements()) {
                        String element = tokenizer.nextToken();
                        if (message == null) {
                            message = new TextMessage(element).setStyle(new MessageStyle().setColor(ChatColor.RED));
                        } else {
                            message.addExtra(new TextMessage(" " + element).setStyle(
                                    new MessageStyle().setColor(ChatColor.GRAY)));
                        }
                    }
                } else {
                    message = new TextMessage(MAINE_KEY + ">")
                            .setStyle(new MessageStyle().setColor(ChatColor.DARK_RED));
                    message.addExtra(new TextMessage(buffer.toString()));
                    if (session != null) {
                        session.send(new ClientChatPacket(buffer.toString()));
                    }

                }
                if (message != null)
                    messages.add(message);
                buffer.setLength(0);
            }
        } else if (input.backspace.clicked && buffer.length() > 0) {
            buffer.setLength(buffer.length() - 1);
        } else if (input.character.hasCharacter()) {
            String msg = createInputString() + MAINE_KEY;
            if ((msg.length() * 8) + 32 >= Game.WIDTH) {
                Sound.test.play();
            } else {
                buffer.append(input.character.ch);
            }
        }
        ++tickCount;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    private String createInputString() {
        return ">" + buffer.toString();
    }

    public void render(Screen screen) {
        screen.clear(0);
        renderMessages(screen);
        String msg = createInputString();
        int xx = 8;
        int yy = (Game.HEIGHT - 2 * 8);
        int w = msg.length() + 1;
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
        Font.draw(msg, screen, xx, yy, col);
        if ((tickCount / 20) % 2 == 0) {
            screen.render(xx + (msg.length() * 8), yy, 28 * 32, Color.get(5, 333, 333, 333), 0);
        } else {
            screen.render(xx + (msg.length() * 8), yy, 28 * 32, col, 0);
        }
    }

    private boolean moreMessages() {
        return messages.size() > maxMessages();
    }

    private int maxMessages() {
        return (Game.HEIGHT - 8) / 8;
    }

    private void renderMessages(Screen screen) {
        if (messages.size() > 0) {
            int x = 0;
            int y = 0;

            final List<Message> view;
            if (moreMessages()) {
                int index = messages.size() - maxMessages();
                view = messages.stream().skip(index).limit(messages.size() - index).collect(Collectors.toList());
            } else {
                view = messages.stream().collect(Collectors.toList());
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
            case BLUE:
                return Color.get(-1, 005, 055, 005);
            case DARK_GREEN:
                return Color.get(-1, 050, 050, 050);
            case DARK_GRAY:
                return Color.get(-1, 333, 333, 333);
            case GRAY:
                return Color.get(-1, 333, 333, 333);
        }
        return Color.get(0, 555, 555, 555);
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
