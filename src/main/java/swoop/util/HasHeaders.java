package swoop.util;

import java.util.List;

public interface HasHeaders {
    /**
     * Retrieve the value single HTTP header.
     * <p/>
     * If the header is not found, null is returned.
     * <p/>
     * If there are multiple headers with the same name, it will return one of them, but it is not
     * defined which one. Instead, use {@link #headers(String)}.
     */
    String header(String name);

    /**
     * Retrieve all values for an HTTP header. If no values are found, an empty List is returned.
     */
    List<String> headers(String name);

    /**
     * Whether a specific HTTP header was present in the request.
     */
    boolean hasHeader(String name);
}
