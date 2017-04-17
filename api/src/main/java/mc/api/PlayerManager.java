package mc.api;

public interface PlayerManager {

    boolean isLoggined(String username);

    void login(String username);

    void remove(String username);
}
