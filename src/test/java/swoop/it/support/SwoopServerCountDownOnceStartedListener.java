package swoop.it.support;

import java.util.concurrent.CountDownLatch;

import swoop.server.SwoopServer;
import swoop.server.SwoopServerListenerAdapter;

public class SwoopServerCountDownOnceStartedListener extends SwoopServerListenerAdapter {

    private CountDownLatch latch;
    
    public SwoopServerCountDownOnceStartedListener(CountDownLatch latch) {
        this.latch = latch;
    }
    
    @Override
    public void serverStarted(SwoopServer server) {
        latch.countDown();
    }
}
