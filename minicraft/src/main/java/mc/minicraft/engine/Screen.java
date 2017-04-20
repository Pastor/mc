package mc.minicraft.engine;

public interface Screen {

    int w();

    int h();

    void clear(int color);

    void setOffset(int xOffset, int yOffset);

    void overlay(Screen screen, int xa, int ya);

    void render(int xp, int yp, int tile, int colors, int bits);

    void renderLight(int x, int y, int r);

    void draw(String msg, int x, int y, int col);
}
