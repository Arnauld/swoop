package swoop.server;

public interface SwoopServerListener {
    void serverStarting(SwoopServer server);
    void serverStarted(SwoopServer server);
    void serverStopping(SwoopServer server);
    void serverStopped(SwoopServer server);
}
