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

    /**
     * Add a listener on the server
     * @param listener
     */
    void addListener(SwoopServerListener listener);
    
    /**
     * Remove the listener from the server
     * @param listener
     */
    void removeListener(SwoopServerListener listener);


}
