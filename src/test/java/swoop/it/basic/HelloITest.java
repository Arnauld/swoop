package swoop.it.basic;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static swoop.Swoop.get;
import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;

import java.util.concurrent.CountDownLatch;

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

public class HelloITest {
    
    private int port;
    private WebTester webTester;

    @BeforeClass
    public void startServer () throws InterruptedException {
        port = PortProvider.acquire();
        CountDownLatch latch = new CountDownLatch(1);
        setPort(port);
        listener(new SwoopServerCountDownOnceStartedListener(latch));
        get(new Action() {
            @Override
            public void handle(Request request, Response response) {
                response.body("<h1>Hello!</h1>");
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
    public void connectAndCheckBody () {
        webTester.beginAt("/");
        String pageSource = webTester.getPageSource();
        assertThat(pageSource, equalTo("<h1>Hello!</h1>"));
        //System.out.println(webTester.getServerResponse());
    }
}
