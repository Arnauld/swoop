package swoop.it.basic;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static swoop.Swoop.get;
import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;
import static swoop.testutil.hamcrest.StringMatchers.stringContains;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwebunit.junit.WebTester;

import org.hamcrest.CoreMatchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.Action;
import swoop.Cookie;
import swoop.Request;
import swoop.Response;
import swoop.it.support.PortProvider;
import swoop.it.support.SwoopServerCountDownOnceStartedListener;

public class OneCookieITest {
    
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
                Cookie cookie = response.createCookie("swoop", "Woot woot!");
                response.cookie(cookie);
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

        assertThat(webTester.getServerResponse(), stringContains("Set-Cookie: swoop=\"Woot woot!\""));
        Pattern cookiePattern = Pattern.compile(".*(Set\\-Cookie: [^\r\n]*).*");
        Matcher matcher = cookiePattern.matcher(webTester.getServerResponse());
        assertThat(matcher.find(), is(true));
        assertThat(matcher.group(1), stringContains("Set-Cookie: swoop=\"Woot woot!\""));
        assertThat(matcher.group(1), stringContains("HTTPOnly;"));
    }
}
