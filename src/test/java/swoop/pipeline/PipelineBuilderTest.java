package swoop.pipeline;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.util.New;

public class PipelineBuilderTest {

    private static Logger logger = LoggerFactory.getLogger(PipelineBuilderTest.class);
    private List<PathMatcher> matchers;

    @BeforeMethod
    public void setUp () {
        matchers = New.arrayList();
    }
    
    @Test
    public void usecase_notThreaded() {
        PipelineBuilder builder = new PipelineBuilder();
        builder.handler(e(new Filter("time"))) //
                .handler(e(new Downstream("auth"))) //
                .handler(e(new Downstream("prepare"))) //
                .handler(e(new Upstream("gzip"))) //
                .handler(e(new Target("bob")))//
        ;

        Pipeline pipeline = builder.buildPipeline().with(StringBuilder.class, new StringBuilder());
        pipeline.invokeNext();

        String string = pipeline.get(StringBuilder.class).toString();
        assertThat(string, equalTo("<time><auth><prepare><bob><<bob>></bob></gzip></time>"));
    }

    private HandlerEntry e(Handler target) {
        return new HandlerEntry(pathMatcherMock(), target);
    }

    private PathMatcher pathMatcherMock() {
        PathMatcher pathMatcher = Mockito.mock(PathMatcher.class);
        matchers.add(pathMatcher);
        return pathMatcher;
    }

    @Test
    public void usecase_threaded() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        PipelineBuilder builder = new PipelineBuilder();
        builder.executor(Pipelines.threadedExecutor())//
                .handler(e(new UpstreamLatch(latch)))//
                .handler(e(new Filter("time"))) //
                .handler(e(new Downstream("auth"))) //
                .handler(e(new Downstream("prepare"))) //
                .handler(e(new Upstream("gzip"))) //
                .handler(e(new Target("bob")))//
        ;

        Pipeline pipeline = builder.buildPipeline().with(StringBuilder.class, new StringBuilder());
        pipeline.invokeNext();
        latch.await();

        String string = pipeline.get(StringBuilder.class).toString();
        assertThat(string, equalTo("<time><auth><prepare><bob><<bob>></bob></gzip></time>"));
    }

    @Test
    public void usecase_threaded_withAsyncJob() throws InterruptedException {
        ExecutorService backgroundExecutor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        PipelineBuilder builder = new PipelineBuilder();
        builder.executor(Pipelines.threadedExecutor())//
                .handler(e(new UpstreamLatch(latch)))//
                .handler(e(new Perf()))//
                .handler(e(new Filter("time"))) //
                .handler(e(new Downstream("auth"))) //
                .handler(e(new Downstream("prepare"))) //
                .handler(e(new Upstream("gzip"))) //
                .handler(e(new Async("bob", backgroundExecutor)))//
        ;

        Pipeline pipeline = builder.buildPipeline().with(StringBuilder.class, new StringBuilder());
        pipeline.invokeNext();
        latch.await();

        String string = pipeline.get(StringBuilder.class).toString();
        assertThat(string, equalTo("<time><auth><prepare><<bob>></gzip></time>"));
    }

    public static class Perf implements PipelineDownstreamHandler, PipelineUpstreamHandler {
        @Override
        public void handleDownstream(Pipeline pipeline) {
            pipeline.with(new Chrono().start()).invokeNext();
        }

        @Override
        public void handleUpstream(Pipeline pipeline) {
            Chrono chrono = pipeline.get(Chrono.class).end();
            logger.info(chrono.elapsed() + "ms");
            pipeline.invokeNext();
        }
    }

    static class Chrono {
        long start, end;

        public Chrono start() {
            this.start = System.currentTimeMillis();
            return this;
        }

        public Chrono end() {
            this.end = System.currentTimeMillis();
            return this;
        }

        public long elapsed() {
            return (end - start);
        }
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
