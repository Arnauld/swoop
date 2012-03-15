package swoop.pipeline;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

public class Pipelines {
    
    public static PipelineExecutor callerExecutor() {
        return new PipelineExecutor() {
            @Override
            public void execute(Handler handler, Pipeline pipeline) {
                handler.handle(pipeline);
            }
            @Override
            public void shutdown() {
            }
        };
    }
    
    private static AtomicLong idGen = new AtomicLong();
    private static AtomicLong idGenThread = new AtomicLong();
    
    public static PipelineExecutor threadedExecutor() {
        return new PipelineExecutor() {
            private String id = "0x"+StringUtils.leftPad(Long.toHexString(idGen.incrementAndGet()), 4, '0');
            private ExecutorService executor = Executors.newFixedThreadPool(1, newNamedThreadFactory());
            @Override
            public void execute(final Handler handler, final Pipeline pipeline) {
                if(id.regionMatches(0, Thread.currentThread().getName(), 0, id.length())) {
                    handler.handle(pipeline);
                }
                else
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            handler.handle(pipeline);
                        }
                    });
            }
            private ThreadFactory newNamedThreadFactory() {
                return new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "PipelineExec-" + id+"-"+idGenThread.incrementAndGet());
                    }
                };
            }
            public void shutdown() {
                executor.shutdown();
            }
        };
    }

    public static Handler wrap(final PipelineDownstreamHandler handler) {
        return new Handler() {
            @Override
            public void handle(Pipeline pipeline) {
                handler.handleDownstream(pipeline);
            }
        };
    }
    
    public static Handler wrap(final PipelineUpstreamHandler handler) {
        return new Handler() {
            @Override
            public void handle(Pipeline pipeline) {
                handler.handleUpstream(pipeline);
            }
        };
    }
    
    public static Handler wrap(final PipelineTargetHandler handler) {
        return new Handler() {
            @Override
            public void handle(Pipeline pipeline) {
                handler.handleTarget(pipeline);
            }
        };
    }
    
}
