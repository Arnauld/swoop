package swoop.util;

import java.util.Set;

public interface HasDataParameters {
    /**
     * Arbitrary data that can be stored for the lifetime of the connection.
     * Retrieve data value by key.
     *
     * @see #data(String,String)
     */
    Object data(String key);
    
    /**
     * Arbitrary data that can be stored for the lifetime of the connection.
     * Store data value by key.
     *
     * @see #data(String)
     */
    void data(String key, Object value);
    
    /**
     * Arbitrary data that can be stored for the lifetime of the connection.
     * List data keys.
     *
     * @see #data()
     */
    Set<String> dataKeys();
}
