package samples.quiz.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QuizAppAsyncITest extends QuizAppITest {

    private AtomicInteger idGen = new AtomicInteger();
    private ExecutorService executor;
    private ThreadGroup threadGroup;

    @Override
    protected void initApplication() {
        threadGroup = new ThreadGroup("Quiz") {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught exception [" + t + "]", e);
            }
        };
        executor = Executors.newFixedThreadPool(4, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(threadGroup, r, "Quiz-worker-" + idGen.incrementAndGet());
            }
        });
        quizService = QuizApp.asyncPolicy(executor);
    }

    @Override
    protected void stopApplication() throws Exception {
        super.stopApplication();
        executor.shutdownNow();
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);
    }
}
