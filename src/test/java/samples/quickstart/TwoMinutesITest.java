package samples.quickstart;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;
import static swoop.testutil.hamcrest.LongMatchers.closeTo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwebunit.junit.WebTester;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.it.support.PortProvider;
import swoop.it.support.SwoopServerCountDownOnceStartedListener;

public class TwoMinutesITest {
    
    private static final String[] NO_ARGS = new String[0];
    private int port;
    private WebTester webTester;
    private DateFormat dateFormat;

    @BeforeClass
    public void startServer () throws InterruptedException {
        port = PortProvider.acquire();
        CountDownLatch latch = new CountDownLatch(1);
        setPort(port);
        listener(new SwoopServerCountDownOnceStartedListener(latch));
        TwoMinutes.main(NO_ARGS);
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
        webTester.beginAt("/time");
        dateFormat = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss'-'SSS ZZZ");
    }
    
    private static final long TOLERANCE_MS = 100;
    
    @Test
    public void timeUrl_returnsCurrentTime () throws ParseException {
        Date now = new Date();
        webTester.gotoPage("/time");
        webTester.assertTextPresent("Current time is: ");
        
        Pattern p = Pattern.compile("\\[([^\\]]+)\\]");
        Matcher matcher = p.matcher(webTester.getPageSource());
        assertThat(matcher.find(), is(true));
        String dateString = matcher.group(1);
        
        Date date = dateFormat.parse(dateString);
        assertThat("Got: " + dateString + ", expected: " + dateFormat.format(now) + "+/-" + TOLERANCE_MS, //
                date.getTime(), closeTo(now.getTime(), TOLERANCE_MS));
        
    }
    
    @Test
    public void timeUrl_doesNotReturn_ProcessingDurationInfo () throws ParseException {
        webTester.gotoPage("/time");
        webTester.assertTextPresent("Current time is: ");
        webTester.assertTextNotPresent(" executed in ");
    }
    
    @Test
    public void helloUrl_doesReturn_ProcessingDurationInfo () throws ParseException {
        webTester.gotoPage("/hello/Travis");
        webTester.assertTextPresent("Hello Travis!");
        webTester.assertTextPresent(" executed in ");
    }
}
