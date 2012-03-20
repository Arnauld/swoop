package swoop.pipeline;

public interface PipelineExecutor {
    /**
     * 
     */
    void execute(HandlerAdapter handler, Pipeline pipeline);

    /**
     * 
     */
    void shutdown();

    /**
     * 
     * @param runnable
     */
    void execute(Runnable runnable);
}
