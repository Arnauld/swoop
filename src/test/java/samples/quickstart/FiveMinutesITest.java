package samples.quickstart;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import swoop.it.support.PortProvider;
import swoop.it.support.SwoopServerCountDownOnceStartedListener;
import swoop.util.New;
import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketConnection;
import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketException;
import de.roderick.weberknecht.WebSocketMessage;

public class FiveMinutesITest {

    private static final String[] NO_ARGS = new String[0];
    private Integer port;

    @BeforeClass
    public void startServer() throws InterruptedException {
        port = PortProvider.acquire();
        CountDownLatch latch = new CountDownLatch(1);
        setPort(port);
        listener(new SwoopServerCountDownOnceStartedListener(latch));
        FiveMinutes.main(NO_ARGS);
        latch.await();
    }

    @AfterClass
    public void stopServer() throws InterruptedException {
        stop();
        PortProvider.release(port);
    }

    @Test
    public void webSocket() throws URISyntaxException, WebSocketException {
        final AtomicBoolean opened = new AtomicBoolean();
        final AtomicBoolean closed = new AtomicBoolean();
        final List<String> messagesReceived = New.arrayList();

        URI url = new URI("ws://0.0.0.0:" + port + "/hellowebsocket");
        WebSocket websocket = new WebSocketConnection(url);
        websocket.setEventHandler(new WebSocketEventHandler() {
            public void onOpen() {
                opened.set(true);
            }

            public void onMessage(WebSocketMessage message) {
                messagesReceived.add(message.getText());
            }

            public void onClose() {
                closed.set(true);
            }
        });

        websocket.connect(); // Establish WebSocket Connection
        websocket.send("hello world"); // Send UTF-8 Text
        websocket.close();

        assertThat(opened.get(), is(true));
        assertThat(messagesReceived, equalTo(asList("HELLO WORLD")));
        assertThat(closed.get(), is(true));
    }
}
