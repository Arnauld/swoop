package swoop.pipeline;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

public class PipelineExecutorDefault implements PipelineExecutor {

    private static AtomicLong idGen = new AtomicLong();
    private static AtomicLong idGenThread = new AtomicLong();

    private final long id = idGen.incrementAndGet();
    private final ExecutorService executor = Executors.newFixedThreadPool(1, newNamedThreadFactory());
    
    public ExecutorService getPipelineThread() {
        return executor;
    }
    
    @Override
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    @Override
    public void execute(final HandlerAdapter handler, final Pipeline pipeline) {
        if (inExecutorPipelineThread()) {
            handler.handle(pipeline);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    handler.handle(pipeline);
                }
            });
        }
    }

    private boolean inExecutorPipelineThread() {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof PipelineThread) {
            PipelineThread pipelineThread = (PipelineThread) currentThread;
            return (id == pipelineThread.getPipelineExecutorId());
        }
        return false;
    }

    private ThreadFactory newNamedThreadFactory() {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new PipelineThread(r, id);
            }
        };
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    private static class PipelineThread extends Thread {
        private final long pipelineExecutorId;

        public PipelineThread(Runnable r, long pipelineExecutorId) {
            super(r, "PipelineExec-" + formatId(pipelineExecutorId) + "-" + idGenThread.incrementAndGet());
            this.pipelineExecutorId = pipelineExecutorId;
        }

        public long getPipelineExecutorId() {
            return pipelineExecutorId;
        }

        private static String formatId(long id) {
            return "0x" + StringUtils.leftPad(Long.toHexString(id), 4, '0');
        }
    }
}
