package samples.quickstart;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;

import java.util.concurrent.CountDownLatch;

import net.sourceforge.jwebunit.junit.WebTester;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.it.support.PortProvider;
import swoop.it.support.SwoopServerCountDownOnceStartedListener;

public class HelloITest {
    
    private static final String[] NO_ARGS = new String[0];
    private int port;
    private WebTester webTester;

    @BeforeClass
    public void startServer () throws InterruptedException {
        port = PortProvider.acquire();
        CountDownLatch latch = new CountDownLatch(1);
        setPort(port);
        listener(new SwoopServerCountDownOnceStartedListener(latch));
        Hello.main(NO_ARGS);
        latch.await();
    }
    
    @AfterClass
    public void stopServer () throws InterruptedException {
        stop();
        PortProvider.release(port);
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
