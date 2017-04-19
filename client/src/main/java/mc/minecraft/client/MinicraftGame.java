package mc.minecraft.client;import mc.api.*;import mc.engine.property.*;import mc.engine.tcp.DefaultClient;import mc.engine.tcp.DefaultFactory;import mc.minecraft.client.console.CommandProcessor;import mc.minecraft.client.console.ConsoleManager;import mc.minecraft.client.console.DefaultCommandProcessor;import mc.minecraft.client.console.DefaultConsoleManager;import mc.minecraft.client.gfx.ClientScreen;import mc.minecraft.client.gfx.Font;import mc.minecraft.client.gfx.SpriteSheet;import mc.minecraft.client.screen.*;import mc.minecraft.client.screen.Menu;import mc.minicraft.Constants;import mc.minicraft.MinicraftProtocol;import mc.minicraft.component.LevelHandler;import mc.minicraft.component.crafting.Crafting;import mc.minicraft.component.entity.Entity;import mc.minicraft.component.entity.Mob;import mc.minicraft.component.entity.Player;import mc.minicraft.component.gfx.Color;import mc.minicraft.component.level.Level;import mc.minicraft.component.level.tile.Tile;import mc.minicraft.data.message.Message;import mc.minicraft.packet.ingame.client.player.ClientPlayerAttackPacket;import mc.minicraft.packet.ingame.client.player.ClientPlayerPositionPacket;import mc.minicraft.packet.ingame.client.player.ClientPlayerSettings;import mc.minicraft.packet.ingame.server.ServerChatPacket;import mc.minicraft.packet.ingame.server.ServerJoinGamePacket;import mc.minicraft.packet.ingame.server.ServerSoundEffectPacket;import mc.minicraft.packet.ingame.server.level.ServerStartLevelPacket;import mc.minicraft.packet.ingame.server.level.ServerUpdateLevelPacket;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import javax.imageio.ImageIO;import javax.swing.*;import java.awt.*;import java.awt.image.BufferStrategy;import java.awt.image.BufferedImage;import java.awt.image.DataBufferInt;import java.net.Proxy;final class MinicraftGame        extends Canvas implements Runnable, Game, Session.Listener, PropertyContainer.Listener, PacketConstructor {    private static final DefaultFactory FACTORY = DefaultFactory.instance();    private static final Logger logger = LoggerFactory.getLogger(MinicraftGame.class);    private static final long serialVersionUID = 1L;    private BufferedImage image;    private int[] pixels;    private boolean running = false;    private ClientScreen screen;    private ClientScreen lightScreen;    private InputHandler input = new InputHandler(this);    private final MainMenu mainMenu;    private int[] colors = new int[256];    private int tickCount = 0;    private int gameTime = 0;    private Level level;    private Level[] levels = new Level[5];    private int currentLevel = 3;    public Menu menu;    private int playerDeadTime;    private int pendingLevelChange;    private int wonTimer = 0;    private boolean hasWon = false;    private int drawableFrames = 0;    private int drawableTicks = 0;    private final Client client;    private final PropertyContainer container = new DefaultPropertyContainer();    private final ConsoleManager consoleManager = new DefaultConsoleManager();    private final CommandProcessor processor = new DefaultCommandProcessor(this, container);    private final JFrame frame;    private final MinicraftProtocol protocol = new MinicraftProtocol(MinicraftProtocol.Sub.LOGIN);    private final Sound sound;    private final ClientPlayerHandler playerHandler;    private MinicraftGame(Proxy proxy, boolean development) {        this.client = new DefaultClient(                container.property(PropertyConstants.SERVER_HOSTNAME).asValue(),                container.property(PropertyConstants.SERVER_PORT).asValue(),                protocol,                FACTORY.newSessionFactory(proxy),                this        );        client.session().setFlag(Constants.AUTH_PROXY_KEY, Proxy.NO_PROXY);        client.session().addListener(this);        mainMenu = new MainMenu(container);        mainMenu.consoleMenu.setSession(client.session());        container.addListener(this);        {            container.update(container.property(PropertyConstants.DEVELOPMENT), development);        }        selfUpdate();        frame = new JFrame(Game.NAME);        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);        frame.setLayout(new BorderLayout());        frame.add(this, BorderLayout.CENTER);        frame.pack();        frame.setResizable(false);        frame.setLocationRelativeTo(null);        image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();        sound = (x, y, type) -> {            switch (type) {                case BOSS_DEATH:                    mc.minecraft.client.sound.Sound.bossdeath.play();                    break;                case CRAFT:                    mc.minecraft.client.sound.Sound.craft.play();                    break;                case MONSTER_HURT:                    mc.minecraft.client.sound.Sound.monsterHurt.play();                    break;                case PICKUP:                    mc.minecraft.client.sound.Sound.pickup.play();                    break;                case PLAYER_DEATH:                    mc.minecraft.client.sound.Sound.playerDeath.play();                    break;                case PLAYER_HURT:                    mc.minecraft.client.sound.Sound.playerHurt.play();                    break;                case TEST:                    mc.minecraft.client.sound.Sound.test.play();                    break;            }        };        playerHandler = new ClientPlayerHandler(input, client, this);        Crafting.init(sound, playerHandler, container);    }    @Override    public PropertyReader propertyReader() {        return container;    }    @Override    public ConsoleManager consoleManager() {        return consoleManager;    }    @Override    public CommandProcessor commandProcessor() {        return processor;    }    public void setMenu(Menu menu) {        this.menu = menu;        if (menu != null)            menu.init(this, input);    }    public void start() {        frame.setVisible(true);        running = true;        new Thread(this).start();    }    @Override    public int width() {        return container.property(PropertyConstants.GAME_WIDTH).<Integer>asValue() / scale();    }    @Override    public int height() {        return container.property(PropertyConstants.GAME_HEIGHT).<Integer>asValue() / scale();    }    @Override    public int scale() {        return container.property(PropertyConstants.GAME_SCALE).asValue();    }    public void stop() {        if (client.session().isConnected()) {            client.session().disconnect("Quit");        }        running = false;    }    public void resetGame() {        if (fastQuit) {            fastQuit = false;            return;        }        playerDeadTime = 0;        wonTimer = 0;        gameTime = 0;        hasWon = false;        currentLevel = 3;    }    @Override    public Player player() {        return level.player();    }    @Override    public int gameTime() {        return gameTime;    }    private void init() {        int pp = 0;        for (int r = 0; r < 6; r++) {            for (int g = 0; g < 6; g++) {                for (int b = 0; b < 6; b++) {                    int rr = (r * 255 / 5);                    int gg = (g * 255 / 5);                    int bb = (b * 255 / 5);                    int mid = (rr * 30 + gg * 59 + bb * 11) / 100;                    int r1 = ((rr + mid * 1) / 2) * 230 / 255 + 10;                    int g1 = ((gg + mid * 1) / 2) * 230 / 255 + 10;                    int b1 = ((bb + mid * 1) / 2) * 230 / 255 + 10;                    colors[pp++] = r1 << 16 | g1 << 8 | b1;                }            }        }        resetGame();        setMenu(mainMenu);    }    @Override    public boolean connect(String username, String password, String hostname) {        if (protocol.isAuthorized())            protocol.unauthorize();        protocol.authorize(username, password);        client.session().connect(hostname, true);        return true;    }    @Override    public boolean isConnected() {        return client.session().isConnected();    }    private boolean fastQuit = false;    @Override    public void mainMenu() {        setMenu(mainMenu);        fastQuit = true;    }    public void run() {        long lastTime = System.nanoTime();        double unprocessed = 0;        double nsPerTick = 1000000000.0 / 60;        int frames = 0;        int ticks = 0;        long lastTimer1 = System.currentTimeMillis();        init();        try {            while (running) {                long now = System.nanoTime();                unprocessed += (now - lastTime) / nsPerTick;                lastTime = now;                boolean shouldRender = true;                while (unprocessed >= 1) {                    ticks++;                    tick();                    unprocessed -= 1;                    shouldRender = true;                }                try {                    Thread.sleep(2);                } catch (InterruptedException e) {                    e.printStackTrace();                }                if (shouldRender) {                    frames++;                    render();                }                if (System.currentTimeMillis() - lastTimer1 > 1000) {                    lastTimer1 += 1000;                    drawableFrames = frames;                    drawableTicks = ticks;                    frames = 0;                    ticks = 0;                }            }        } catch (Throwable ex) {            ex.printStackTrace();        }        frame.dispose();    }    public void tick() {        tickCount++;        if (!hasFocus()) {            input.releaseAll();        } else {            Player player = level != null ? level.player() : null;            if (player != null && !player.removed && !hasWon)                gameTime++;            input.tick();            if (menu != null) {                menu.tick();            } else if (player != null) {                if (player.removed) {                    fastQuit = false;                    playerDeadTime++;                    if (playerDeadTime > 60) {                        setMenu(new DeadMenu(mainMenu));                    }                } else {                    if (pendingLevelChange != 0) {                        setMenu(new LevelTransitionMenu(pendingLevelChange, container));                        pendingLevelChange = 0;                    }                }                if (wonTimer > 0) {                    if (--wonTimer == 0) {                        setMenu(new WonMenu(mainMenu));                    }                }                Point move = playerHandler.move();                if (move.x != 0 || move.y != 0) {                    ClientPlayerPositionPacket packet = new ClientPlayerPositionPacket();                    packet.xa = move.x;                    packet.ya = move.y;                    client.session().send(packet);                } else if (playerHandler.isAttacked()) {                    client.session().send(new ClientPlayerAttackPacket());                } else {                    player.tickMenu();                }                Tile.tickCount++;            }        }    }    public void changeLevel(int dir) {        if (level != null) {            Player player = level.player();            level.remove(player);            currentLevel += dir;            level = levels[currentLevel];            player.x = (player.x >> 4) * 16 + 8;            player.y = (player.y >> 4) * 16 + 8;            level.add(player);        }    }    @Override    public void addKeyListener(InputHandler inputHandler) {        super.addKeyListener(inputHandler);    }    public void render() {        BufferStrategy bs = getBufferStrategy();        if (bs == null) {            createBufferStrategy(3);            requestFocus();            return;        }        Player player = level != null ? level.player() : null;        if (player != null) {            int xScroll = player.x - screen.w / 2;            int yScroll = player.y - (screen.h - 8) / 2;            if (xScroll < 16) xScroll = 16;            if (yScroll < 16) yScroll = 16;            if (xScroll > level.w * 16 - screen.w - 16) xScroll = level.w * 16 - screen.w - 16;            if (yScroll > level.h * 16 - screen.h - 16) yScroll = level.h * 16 - screen.h - 16;            if (currentLevel > 3) {                int col = Color.get(20, 20, 121, 121);                for (int y = 0; y < 14; y++)                    for (int x = 0; x < 24; x++) {                        screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), 0, col, 0);                    }            }            level.renderBackground(screen, xScroll, yScroll);            level.renderSprites(screen, xScroll, yScroll);            if (currentLevel < 3) {                lightScreen.clear(0);                level.renderLight(lightScreen, xScroll, yScroll);                screen.overlay(lightScreen, xScroll, yScroll);            }        } else if (menu == null) {            setMenu(mainMenu);        }        renderGui();        if (!hasFocus()) {            renderFocusNagger();        }        int yNext = 0;        if (container.property(PropertyConstants.SHOW_FPS).asValue()) {            String msg = String.format("FPS: %02d, FRAMES: %03d", drawableTicks, drawableFrames);            Font.draw(msg, screen, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));            yNext += 8;        }        {            final String msg;            final int color;            if (protocol.isAuthorized() && client.session().isConnected()) {                msg = String.format("%s", protocol.profile.name);                color = Color.get(-1, 505, 505, 505);            } else {                msg = "Нет соединения";                color = Color.get(-1, 500, 500, 500);            }            Font.draw(msg, screen, (width() - 8) - msg.length() * 8, yNext, color);        }        for (int y = 0; y < screen.h; y++) {            for (int x = 0; x < screen.w; x++) {                int cc = screen.pixels[x + y * screen.w];                if (cc < 255) pixels[x + y * width()] = colors[cc];            }        }        Graphics g = bs.getDrawGraphics();        g.fillRect(0, 0, getWidth(), getHeight());        int ww = width() * scale();        int hh = height() * scale();        int xo = (getWidth() - ww) / 2;        int yo = (getHeight() - hh) / 2;        g.drawImage(image, xo, yo, ww, hh, null);        g.dispose();        bs.show();    }    private void renderGui() {        for (int y = 0; y < 2; y++) {            for (int x = 0; x < 20; x++) {                screen.render(x * 8, screen.h - 16 + y * 8, 0 + 12 * 32, Color.get(000, 000, 000, 000), 0);            }        }        Player player = level != null ? level.player() : null;        if (player != null) {            for (int i = 0; i < 10; i++) {                if (i < player.health)                    screen.render(i * 8, screen.h - 16, 0 + 12 * 32, Color.get(000, 200, 500, 533), 0);                else                    screen.render(i * 8, screen.h - 16, 0 + 12 * 32, Color.get(000, 100, 000, 000), 0);                if (player.staminaRechargeDelay > 0) {                    if (player.staminaRechargeDelay / 4 % 2 == 0)                        screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 555, 000, 000), 0);                    else                        screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);                } else {                    if (i < player.stamina)                        screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 220, 550, 553), 0);                    else                        screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);                }            }            if (player.activeItem != null) {                player.activeItem.renderInventory(screen, 10 * 8, screen.h - 16);            }        }        if (menu != null) {            menu.render(screen);        }    }    private void renderFocusNagger() {        renderSplashMessage("Click to focus!");    }    private void renderSplashMessage(String msg) {        int xx = (width() - msg.length() * 8) / 2;        int yy = (height() - 8) / 2;        int w = msg.length();        int h = 1;        screen.render(xx - 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 0);        screen.render(xx + w * 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 1);        screen.render(xx - 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 2);        screen.render(xx + w * 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 3);        for (int x = 0; x < w; x++) {            screen.render(xx + x * 8, yy - 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0);            screen.render(xx + x * 8, yy + 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2);        }        for (int y = 0; y < h; y++) {            screen.render(xx - 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0);            screen.render(xx + w * 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1);        }        if ((tickCount / 20) % 2 == 0) {            Font.draw(msg, screen, xx, yy, Color.get(5, 333, 333, 333));        } else {            Font.draw(msg, screen, xx, yy, Color.get(5, 555, 555, 555));        }    }    public void scheduleLevelChange(int dir) {        pendingLevelChange = dir;    }    public void disconnected() {        client.session().disconnect("Quit");    }    public static MinicraftGame startGame(Proxy proxy, boolean development) {        MinicraftGame game = new MinicraftGame(proxy, development);        game.start();        return game;    }    public void won() {        wonTimer = 60 * 3;        hasWon = true;        fastQuit = false;    }    @Override    public void packetReceived(Session.Event event) {        if (event.packet() instanceof ServerStartLevelPacket) {            ServerStartLevelPacket packet = event.asPacket();            level = packet.readLevel(sound, container, playerHandler);            Player player = new Player(sound, playerHandler, container);            player.x = packet.xPlayer;            player.y = packet.yPlayer;            player.id = packet.id;            level.owner = packet.id;            level.add(player);            setMenu(null);            sound.play(player.x, player.y, Sound.Type.TEST);        } else if (event.packet() instanceof ServerJoinGamePacket) {            int distance = Math.max(width(), height()) / 8;            event.session.send(new ClientPlayerSettings(distance));        } else if (event.packet() instanceof ServerSoundEffectPacket) {            ServerSoundEffectPacket effect = event.asPacket();            sound.play(0, 0, effect.type);        } else if (event.packet() instanceof ServerUpdateLevelPacket) {            ServerUpdateLevelPacket update = event.asPacket();            for (LevelHandler.DataKey key : update.tiles) {                int data = level.getData(key.x, key.y);                level.setTile(key.x, key.y, Tile.tiles[key.value], data);            }            for (LevelHandler.DataKey key : update.datas) {                level.setData(key.x, key.y, key.value);            }            for (Entity entity : update.removeEntities) {                level.remove(entity);            }            for (Entity entity : update.insertEntities) {                level.add(entity);                System.out.println(String.format("Entity[%20s] %15s, X: %5d, Y: %5d, Dist: %5d",                        entity.id.toString().toUpperCase(), entity.getClass().getSimpleName(),                        entity.x, entity.y, entity instanceof Mob ? ((Mob) entity).walkDist : 0));            }        } else if (event.packet() instanceof ServerChatPacket) {            ServerChatPacket packet = event.asPacket();            consoleManager.send(packet.getMessage());        }    }    @Override    public void packetSent(Session.Event event) {    }    @Override    public void connected(Session.Event event) {        consoleManager.send(ConsoleManager.Type.SYSTEM,                String.format("Connected to %s:%d",                        container.property(PropertyConstants.SERVER_HOSTNAME).<String>asValue(),                        container.property(PropertyConstants.SERVER_PORT).<Integer>asValue()                ));    }    @Override    public void disconnecting(Session.DisconnectEvent event) {        setMenu(mainMenu);        level = null;    }    @Override    public void disconnected(Session.DisconnectEvent event) {        disconnected();        logger.info("Disconnected: " + Message.fromString(event.reason).getFullText());        if (event.cause != null) {            event.cause.printStackTrace();        }        consoleManager.send(ConsoleManager.Type.SYSTEM, "Disconnected");    }    @Override    public void update(Property value) {        boolean updateScene = false;        if (PropertyConstants.GAME_WIDTH.equalsIgnoreCase(value.key())) {            updateScene = selfUpdate();        } else if (PropertyConstants.GAME_HEIGHT.equalsIgnoreCase(value.key())) {            updateScene = selfUpdate();        } else if (PropertyConstants.GAME_SCALE.equalsIgnoreCase(value.key())) {            updateScene = selfUpdate();        }        if (updateScene) {            updateScene = false;            frame.invalidate();            frame.pack();            int distance = Math.max(width(), height()) / 8;            client.session().send(new ClientPlayerSettings(distance));        }    }    private boolean selfUpdate() {        int xWidth = width() * scale();        int xHeight = height() * scale();        Dimension size = new Dimension(xWidth, xHeight);        setMinimumSize(size);        setMaximumSize(size);        setPreferredSize(size);        image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();        try {            screen = new ClientScreen(width(), height(),                    new SpriteSheet(ImageIO.read(MinicraftGame.class.getResourceAsStream("/icons.png"))));            lightScreen = new ClientScreen(width(), height(),                    new SpriteSheet(ImageIO.read(MinicraftGame.class.getResourceAsStream("/icons.png"))));        } catch (Exception ex) {            ex.printStackTrace();        }        return true;    }    @Override    public void contruct(Session session, Packet packet) {        if (packet instanceof ServerUpdateLevelPacket) {            ServerUpdateLevelPacket p = (ServerUpdateLevelPacket) packet;            p.handler = playerHandler;            p.sound = sound;            p.propertyReader = container;        }    }}