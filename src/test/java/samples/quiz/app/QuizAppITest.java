package samples.quiz.app;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.pattern.Patterns.anyCharacterInCategory;
import static org.hamcrest.text.pattern.Patterns.oneOrMore;
import static org.hamcrest.text.pattern.Patterns.sequence;
import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;
import static swoop.testutil.hamcrest.StringMatchers.uuidMatcher;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.hamcrest.text.pattern.PatternComponent;
import org.hamcrest.text.pattern.PatternMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import samples.Json;
import samples.quiz.infra.Result;
import samples.quiz.service.QuizServiceInMemory;
import swoop.it.support.PortProvider;
import swoop.it.support.SwoopServerCountDownOnceStartedListener;
import swoop.util.New;
import fj.Effect;

public class QuizAppITest {

    protected Logger logger = LoggerFactory.getLogger(QuizAppITest.class);

    private Integer port;
    private DefaultHttpClient httpClient;
    private String baseUrl;

    protected QuizServiceInMemory quizService;
    //
    private UncaughtExceptionHandler uncaughtExceptionHandler;
    private volatile Throwable asyncException;
    //
    private static final int PARALLELISM = 10;
    private static final int NUMBER_CREATED_THROUGH_TEST_API = 100;

    @BeforeClass
    public void startServer() throws Exception {
        port = PortProvider.acquire();
        baseUrl = "http://0.0.0.0:" + port;
        CountDownLatch latch = new CountDownLatch(1);
        setPort(port);
        listener(new SwoopServerCountDownOnceStartedListener(latch));
        initApplication();

        uncaughtExceptionHandler = new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught exception [" + t + "]", e);
                asyncException = e;
            }
        };
        latch.await();
    }

    protected void initApplication() throws Exception {
        quizService = QuizApp.directPolicy();
    }

    @AfterClass
    public void stopServer() throws Exception {
        stop();
        stopApplication();
        PortProvider.release(port);
    }

    protected void stopApplication() throws Exception {
    }

    @BeforeMethod
    public void initConnectionAndResetServiceContent() {
        // concurrent tests create NUMBER_CREATED_THROUGH_TEST_API * COUNT quizzes
        // setting the initial size, will prevent map to be rehashed during tests
        quizService.clearContent(1500);
        httpClient = new DefaultHttpClient();
        asyncException = null;
    }

    @AfterMethod
    public void ensureNoAsyncException() {
        assertThat(asyncException, nullValue());
    }

    @Test
    public void createTestData() throws Exception {
        HttpResponse response = httpClient.execute(new HttpPost(baseUrl + "/api/create-test-data"));
        Result result = readResultAndAssertIsOk(response);
        assertResultContainsValidQuizzes(result, NUMBER_CREATED_THROUGH_TEST_API);
    }

    @Test
    public void createTestDataUsingFlow() throws Exception {
        HttpResponse response = httpClient.execute(new HttpPost(baseUrl + "/api/create-flow-data"));
        Result result = readResultAndAssertIsOk(response);
        assertResultContainsValidQuizzes(result, NUMBER_CREATED_THROUGH_TEST_API);
    }

    @Test
    public void createTestData_concurrently() throws Exception {
        final CyclicBarrier begBarrier = new CyclicBarrier(PARALLELISM + 1);
        final CyclicBarrier endBarrier = new CyclicBarrier(PARALLELISM + 1);
        for (int i = 0; i < PARALLELISM; i++) {
            Thread thread = new Thread(createTestDataAsRunnable(begBarrier, endBarrier));
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            thread.start();
        }

        // wait all spawned threads are blocking on the begBarrier
        await(begBarrier); // unleach them if they are all ready

        // wait all spawned threads are blocking on the endBarrier
        await(endBarrier); // now every one should have created their test data

        HttpResponse response = httpClient.execute(new HttpGet(baseUrl + "/api/quizzes"));
        Result result = readResultAndAssertIsOk(response);
        assertResultContainsValidQuizzes(result, PARALLELISM * NUMBER_CREATED_THROUGH_TEST_API);
    }
    
    @Test
    public void createTestData_pickOne_and_deleteIt() throws Exception {
        HttpResponse response = httpClient.execute(new HttpPost(baseUrl + "/api/create-test-data"));
        Result result = readResultAndAssertIsOk(response);
        
        CollectorEffect<String> uuidCollector = new CollectorEffect<String>();
        assertResultContainsValidQuizzes(result, NUMBER_CREATED_THROUGH_TEST_API, uuidCollector);
        String uuid = uuidCollector.random();
        
        response = httpClient.execute(new HttpDelete(baseUrl + "/api/quizzes/" + uuid));
        result = readResultAndAssertIsOk(response);
    }

    protected Runnable createTestDataAsRunnable(final CyclicBarrier begBarrier, final CyclicBarrier endBarrier) {
        return new Runnable() {
            @Override
            public void run() {
                await(begBarrier);
                try {
                    // httpClient cannot be shared
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    httpClient.execute(new HttpPost(baseUrl + "/api/create-flow-data"));
                } catch (ClientProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                await(endBarrier);
            }
        };
    }

    protected static void await(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Result readResultAndAssertIsOk(HttpResponse response) throws IOException {
        String rawContent = EntityUtils.toString(response.getEntity());
        Result result = Json.fromJson(rawContent, Result.class);
        assertThat(result.getCode(), equalTo("ok"));
        return result;
    }

    protected static void assertResultContainsValidQuizzes(Result result, final int COUNT) {
        assertResultContainsValidQuizzes(result, COUNT, new NullEffect<String>());
    }
    
    @SuppressWarnings("unchecked")
    protected static void assertResultContainsValidQuizzes(Result result, final int COUNT, Effect<String> uuidCallback) {
        // by default jackson unserialized to LinkedHashMap
        // ~ payload is 'Object' thus no way to figure out what is the real underlying class
        assertThat("by default jackson unserialized to LinkedHashMap", result.getPayload(),
                instanceOf(LinkedHashMap.class));

        Map<String, Object> payload = (Map<String, Object>) result.getPayload();
        assertThat(payload.get("elements"), instanceOf(List.class));

        List<Object> elements = (List<Object>) payload.get("elements");
        assertThat("Test data contains " + COUNT + " quizzes", elements.size(), equalTo(COUNT));

        for (int i = 0; i < COUNT; i++) {
            assertThat("by default jackson unserialized to LinkedHashMap", elements.get(i),
                    instanceOf(LinkedHashMap.class));
            Map<String, Object> quiz = (Map<String, Object>) elements.get(i);

            assertThat((String) quiz.get("title"), testTitleMatcher());
            assertThat((String) quiz.get("id"), uuidMatcher());
            uuidCallback.e((String) quiz.get("id"));
        }
    }

    private static PatternMatcher testTitleMatcher() {
        PatternComponent digit = anyCharacterInCategory("Digit");
        return new PatternMatcher(sequence("Test Quizz #", oneOrMore(digit)));
    }
    
    public static class NullEffect<T> extends Effect<T> {
        @Override
        public void e(T a) {
        }
    }
    
    public static class CollectorEffect<T> extends Effect<T> {
        private ConcurrentLinkedQueue<T> elements = New.concurrentLinkedQueue();
        @Override
        public void e(T element) {
            elements.add(element);
        }
        public ConcurrentLinkedQueue<T> getElements() {
            return elements;
        }
        public T random() {
            int index = new Random().nextInt(elements.size());
            Iterator<T> iterator = elements.iterator();
            T elem = null;
            while(index-- > 0) {
                elem = iterator.next();
            }
            return elem;
        }
    }
}
