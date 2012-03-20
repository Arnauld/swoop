package swoop.pipeline;


public interface Pipeline {

    /**
     * 
     */
    void invokeNext();

    /**
     * 
     */
    <T> T get(Class<T> type);

    /**
     * 
     */
    <T> Pipeline with(Class<T> type, T value);
    
    /**
     * 
     */
    <T> Pipeline with(T value);
    
    /**
     * 
     */
    void execute(Runnable runnable);
}
