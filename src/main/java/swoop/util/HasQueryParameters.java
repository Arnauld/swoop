package swoop.util;

import java.util.List;
import java.util.Set;

public interface HasQueryParameters {
    /**
     * Get query parameter value.
     *
     * @param key parameter name
     * @return the value of the parameter
     * @see #queryParams(String)
     */
    String queryParam(String key);

    /**
     * Get all query parameter values.
     *
     * @param key parameter name
     * @return the values of the parameter
     * @see #queryParam(String)
     */
    List<String> queryParams(String key);

    /**
     * List all query parameter keys.
     *
     * @see #queryParam(String)
     */
    Set<String> queryParamKeys();
}
