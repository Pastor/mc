package com.mojang.ld22.screen;

import com.google.common.collect.EvictingQueue;
import com.mojang.ld22.Game;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

final class ChatMenu extends Menu {
    private final String username;
    private final Menu parent;
    private final EvictingQueue<Message> messages = EvictingQueue.create(100);
    private final StringBuilder buffer = new StringBuilder();

    ChatMenu(String username, Menu parent) {
        this.username = username;
        this.parent = parent;
    }

    public void tick() {
        if (input.escape.clicked) {
            game.setMenu(parent);
        } else if (input.enter.clicked) {
            if (buffer.length() > 0) {
                messages.add(new Message(username, buffer.toString()));
                buffer.setLength(0);
            }
        } else if (input.backspace.clicked && buffer.length() > 0) {
            buffer.setLength(buffer.length() - 1);
        } else if (input.character.hasCharacter()) {
            String msg = createInputString() + username;
            if ((msg.length() * 8) + 32 >= Game.WIDTH) {
                Sound.test.play();
            } else {
                buffer.append(input.character.ch);
            }
        }
    }

    void addMessage(String username, String message) {
        messages.add(new Message(username, message));
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
        int w = msg.length();
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
        Font.draw(msg, screen, xx, yy, Color.get(5, 555, 555, 555));
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
                int col = userColor(message.username);
                Font.draw(message.username, screen, x, y, col);
                x += message.username.length() * 8;
                Font.draw(">", screen, x, y, col);
                x += 2 * 8;
                Font.draw(message.message, screen, x, y, Color.get(0, 555, 555, 555));
                x = 0;
                y += 8;
            }
        }
    }

    private int userColor(String username) {
        Integer color = userColor.get(username);
        if (color == null) {
            if ("ПАСТОР".equalsIgnoreCase(username)) {
                color = Color.get(-1, 500, 500, 500);
            } else {
                Random r = new Random();
                int b = r.nextInt(50) + 50;
                int c = r.nextInt(50) + 50;
                int d = r.nextInt(50) + 50;
                color = Color.get(-1, b, c, d);
            }
            userColor.put(username, color);
        }
        return color;
    }

    private final Map<String, Integer> userColor = new HashMap<>();

    private static final class Message {
        final String username;
        final String message;

        private Message(String username, String message) {
            this.username = username;
            this.message = message;
        }
    }
}
