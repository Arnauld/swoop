package swoop.pipeline;

public interface PipelineUpstreamHandler extends Handler {
    void handleUpstream(Pipeline pipeline);
}
