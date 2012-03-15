package swoop.pipeline;

public interface PipelineExecutor {
    void execute(Handler handler, Pipeline pipeline);
    /**
     * 
     */
    void shutdown();
}
