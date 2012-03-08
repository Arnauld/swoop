package swoop;

import swoop.util.HasDataParameters;


public interface WebSocketConnection extends HasDataParameters {

    /**
     * Sends a text frame
     *
     * @param message frame payload
     * @return this
     */
    WebSocketConnection send(String message);

    /**
     * Sends a binary frame
     *
     * @param message frame payload
     * @return this
     */
    WebSocketConnection send(byte[] message);

    /**
     * Sends a ping frame
     *
     * @param message the payload of the ping
     * @return this
     */
    WebSocketConnection ping(byte[] message);

    /**
     * Sends a pong frame
     *
     * @param message the payload of the ping
     * @return this
     */
    WebSocketConnection pong(byte[] message);

    /**
     * Return the underlying request
     */
    Request request();
}
