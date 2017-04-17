package mc.api;

public interface Client {
    String host();

    int port();

    Protocol protocol();

    Session session();

}
