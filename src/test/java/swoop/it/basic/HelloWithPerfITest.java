package swoop.it.basic;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static swoop.Swoop.around;
import static swoop.Swoop.get;
import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.jwebunit.junit.WebTester;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.Action;
import swoop.Request;
import swoop.Response;
import swoop.it.support.PortProvider;
import swoop.it.support.SwoopServerCountDownOnceStartedListener;
import swoop.support.PerfFilter;

public class HelloWithPerfITest {
    
    private int port;
    private WebTester webTester;
    private PerfFilter perfFilter;
    private AtomicInteger idGen = new AtomicInteger();

    @BeforeClass
    public void startServer () throws InterruptedException {
        port = PortProvider.acquire();
        CountDownLatch latch = new CountDownLatch(1);
        setPort(port);
        listener(new SwoopServerCountDownOnceStartedListener(latch));
        perfFilter = new PerfFilter();
        around(perfFilter);
        get(new Action() {
            @Override
            public void handle(Request request, Response response) {
                response.body("<h1>Hello! #" + idGen.incrementAndGet() + " times</h1>");
            }
        });
        latch.await();
    }
    
    @AfterClass
    public void stopServer () {
        stop();
    }
    
    @BeforeMethod
    public void initWebTester () {
        webTester = new WebTester();
        webTester.setBaseUrl("http://localhost:" + port);
    }
    
    @Test
    public void connectThreeTimesAndCheckStats () {
        webTester.beginAt("/");
        assertThat(webTester.getPageSource(), equalTo("<h1>Hello! #1 times</h1>"));
        assertThat(perfFilter.grab().getInvokeCount(), equalTo(1L));
        webTester.gotoPage("/");
        assertThat(webTester.getPageSource(), equalTo("<h1>Hello! #2 times</h1>"));
        webTester.gotoPage("/");
        assertThat(webTester.getPageSource(), equalTo("<h1>Hello! #3 times</h1>"));
        assertThat(perfFilter.grab().getInvokeCount(), equalTo(3L));

    }
}
