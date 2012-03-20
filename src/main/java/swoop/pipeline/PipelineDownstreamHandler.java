package swoop.pipeline;

public interface PipelineDownstreamHandler extends Handler {
    void handleDownstream(Pipeline pipeline);
}
