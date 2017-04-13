package mc.api;

public interface Client {
    String host();

    int port();

    Provider<Protocol> protocol();

    Session session();

}
