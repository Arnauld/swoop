package swoop.pipeline;

public class Pipelines {

    public static PipelineExecutor callerExecutor() {
        return new PipelineExecutor() {
            @Override
            public void execute(HandlerAdapter handler, Pipeline pipeline) {
                handler.handle(pipeline);
            }

            @Override
            public void execute(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void shutdown() {
            }
        };
    }

    public static PipelineExecutor threadedExecutor() {
        return new PipelineExecutorDefault();
    }
}
