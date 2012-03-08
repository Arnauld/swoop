package swoop.route;

public interface FilterAware {
    /**
     * Indicates whether this route must be considered as a final target or as a filter (aka interceptor).
     * 
     * @return <code>true</code> if this route must be considered as a filter.
     */
    boolean isFilter();
}
