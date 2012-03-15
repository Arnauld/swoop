package swoop.pipeline;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import swoop.util.ContextBasic;

public class PipelineBuilderTest {

    private static Logger logger = LoggerFactory.getLogger(PipelineBuilderTest.class);

    @Test
    public void usecase_notThreaded() {
        PipelineBuilder builder = new PipelineBuilder();
        ContextBasic context = new ContextBasic().register(StringBuilder.class, new StringBuilder());
        builder.context(context) //
                .handler(new Filter("time")) //
                .handler(new Downstream("auth")) //
                .handler(new Downstream("prepare")) //
                .handler(new Upstream("gzip")) //
                .handler(new Target("bob"));

        builder.buildPipeline().invokeNext();

        String string = context.get(StringBuilder.class).toString();
        assertThat(string, equalTo("<time><auth><prepare><bob><<bob>></bob></gzip></time>"));
    }

    @Test
    public void usecase_threaded() throws InterruptedException {
        ContextBasic context = new ContextBasic().register(StringBuilder.class, new StringBuilder());
        CountDownLatch latch = new CountDownLatch(1);

        PipelineBuilder builder = new PipelineBuilder();
        builder.context(context) //
                .executor(Pipelines.threadedExecutor())//
                .handler(new UpstreamLatch(latch))//
                .handler(new Filter("time")) //
                .handler(new Downstream("auth")) //
                .handler(new Downstream("prepare")) //
                .handler(new Upstream("gzip")) //
                .handler(new Target("bob"))//
        ;

        builder.buildPipeline().invokeNext();
        latch.await();

        String string = context.get(StringBuilder.class).toString();
        assertThat(string, equalTo("<time><auth><prepare><bob><<bob>></bob></gzip></time>"));
    }

    @Test
    public void usecase_threaded_withAsyncJob() throws InterruptedException {
        ExecutorService backgroundExecutor = Executors.newFixedThreadPool(2);
        ContextBasic context = new ContextBasic().register(StringBuilder.class, new StringBuilder());
        CountDownLatch latch = new CountDownLatch(1);

        PipelineBuilder builder = new PipelineBuilder();
        builder.context(context) //
                .executor(Pipelines.threadedExecutor())//
                .handler(new UpstreamLatch(latch))//
                .handler(new Filter("time")) //
                .handler(new Downstream("auth")) //
                .handler(new Downstream("prepare")) //
                .handler(new Upstream("gzip")) //
                .handler(new Async("bob", backgroundExecutor))//
        ;

        builder.buildPipeline().invokeNext();
        latch.await();

        String string = context.get(StringBuilder.class).toString();
        assertThat(string, equalTo("<time><auth><prepare><<bob>></gzip></time>"));
    }

    public static class Async implements PipelineTargetHandler {
        private String token;
        private Executor backgroundThread;

        public Async(String token, Executor backgroundThread) {
            super();
            this.token = token;
            this.backgroundThread = backgroundThread;
        }

        @Override
        public void handleTarget(final Pipeline pipeline) {
            backgroundThread.execute(new Runnable() {
                @Override
                public void run() {
                    logger.info("Async:Target [" + token + "]");
                    pipeline.get(StringBuilder.class).append("<<").append(token).append(">>");
                    // some jobs...
                    pipeline.invokeNext();
                }
            });
        }
    }

    public static class Filter implements PipelineDownstreamHandler, PipelineUpstreamHandler {
        private String token;

        public Filter(String token) {
            super();
            this.token = token;
        }

        @Override
        public void handleDownstream(Pipeline pipeline) {
            logger.info("Filter vvv{}", token);
            pipeline.get(StringBuilder.class).append("<").append(token).append(">");
            pipeline.invokeNext();
        }

        @Override
        public void handleUpstream(Pipeline pipeline) {
            logger.info("Filter ^^^{}", token);
            pipeline.get(StringBuilder.class).append("</").append(token).append(">");
            pipeline.invokeNext();
        }
    }

    public static class Target implements PipelineDownstreamHandler, PipelineUpstreamHandler, PipelineTargetHandler {
        private String token;

        public Target(String token) {
            super();
            this.token = token;
        }

        @Override
        public void handleTarget(Pipeline pipeline) {
            logger.info("Target {}", token);
            pipeline.get(StringBuilder.class).append("<<").append(token).append(">>");
            pipeline.invokeNext();
        }

        @Override
        public void handleDownstream(Pipeline pipeline) {
            logger.info("Target vvv{}", token);
            pipeline.get(StringBuilder.class).append("<").append(token).append(">");
            pipeline.invokeNext();
        }

        @Override
        public void handleUpstream(Pipeline pipeline) {
            logger.info("Target ^^^{}", token);
            pipeline.get(StringBuilder.class).append("</").append(token).append(">");
            pipeline.invokeNext();
        }
    }

    public static class Downstream implements PipelineDownstreamHandler {
        private String token;

        public Downstream(String token) {
            super();
            this.token = token;
        }

        @Override
        public void handleDownstream(Pipeline pipeline) {
            logger.info("Downstream vvv{}", token);
            pipeline.get(StringBuilder.class).append("<").append(token).append(">");
            pipeline.invokeNext();
        }
    }

    public static class Upstream implements PipelineUpstreamHandler {
        private String token;

        public Upstream(String token) {
            super();
            this.token = token;
        }

        @Override
        public void handleUpstream(Pipeline pipeline) {
            logger.info("Upstream ^^^{}", token);
            pipeline.get(StringBuilder.class).append("</").append(token).append(">");
            pipeline.invokeNext();
        }
    }

    public static class UpstreamLatch implements PipelineUpstreamHandler {
        private CountDownLatch latch;

        public UpstreamLatch(CountDownLatch latch) {
            super();
            this.latch = latch;
        }

        @Override
        public void handleUpstream(Pipeline pipeline) {
            logger.info("Upstream ^^^ latch");
            latch.countDown();
            pipeline.invokeNext();
        }
    }
}
