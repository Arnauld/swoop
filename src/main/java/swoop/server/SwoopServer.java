package swoop.server;

public interface SwoopServer {

    /**
     * Ignites the server listening on the provided port
     * 
     * @param port
     */
    void ignite(int port);

    /**
     * Stops the server
     */
    void stop();

}
