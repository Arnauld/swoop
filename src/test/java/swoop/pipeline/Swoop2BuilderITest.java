package swoop.pipeline;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sourceforge.jwebunit.junit.WebTester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebSocketConnection;

import swoop.it.support.PortProvider;
import swoop.path.Verb;
import swoop.pipeline.webbit.WebbitPipelineServer;
import swoop.route.RouteParameters;
import swoop.util.New;
import de.roderick.weberknecht.WebSocketException;

public class Swoop2BuilderITest {
    
    private Logger logger = LoggerFactory.getLogger(Swoop2BuilderITest.class);

    private WebTester webTester;
    private int port;
    private WebbitPipelineServer server;
    private CountDownLatch webSocketCloseLatch;
    
    @BeforeClass
    public void startServer() throws InterruptedException {
        port = PortProvider.acquire();
        server = new Swoop2Builder() {
            {
                port(port);
                get("/hello/:name", new PipelineTargetHandler() {
                    @Override
                    public void handleTarget(Pipeline pipeline) {
                        RouteParameters routeParameters = pipeline.get(RouteParameters.class);
                        HttpResponse httpResponse = pipeline.get(HttpResponse.class);
                        httpResponse.content("Hello " + routeParameters.routeParam("name"));
                        pipeline.invokeNext();
                    }
                });
                webSocket("/hellowebsocket", new PipelineTargetHandler() {

                    @Override
                    public void handleTarget(Pipeline pipeline) {
                        Verb verb = pipeline.get(Verb.class);
                        logger.info("WebSocket, Verb {}", verb);
                        switch(verb) {
                            case WebSocketOpen:
                            case WebSocketPing:
                            case WebSocketPong:
                                break;
                            case WebSocketClose:
                                pipeline.execute(new Runnable() {
                                   @Override
                                    public void run() {
                                       webSocketCloseLatch.countDown();
                                    } 
                                });
                                break;
                            case WebSocketMessage:
                                MessageReceived received = pipeline.get(MessageReceived.class);
                                WebSocketConnection webSocketConnection = pipeline.get(WebSocketConnection.class);
                                String text = received.textOrBinaryAsText("utf8");
                                webSocketConnection.send(text.toUpperCase());
                        }
                    }
                });
            }
        }.server();
        server.awaitServerStarted();
    }

    @AfterClass
    public void stopServer() throws InterruptedException {
        server.shutdown();
        PortProvider.release(port);
    }
    
    @BeforeMethod
    public void prepare () {
    }
    
    @Test
    public void httpHello() throws InterruptedException {
        webTester = new WebTester();
        webTester.setBaseUrl("http://localhost:" + port);
        webTester.beginAt("/hello/World");
        String pageSource = webTester.getPageSource();
        assertThat(pageSource, equalTo("Hello World"));
    }
    
    @Test
    public void webSocketHello() throws InterruptedException, URISyntaxException, WebSocketException {
        final AtomicBoolean opened = new AtomicBoolean();
        final AtomicBoolean closed = new AtomicBoolean();
        final List<String> messagesReceived = New.arrayList();
        final CountDownLatch messageArrived = new CountDownLatch(1);
        webSocketCloseLatch = new CountDownLatch(1);

        URI url = new URI("ws://0.0.0.0:" + port + "/hellowebsocket");
        de.roderick.weberknecht.WebSocket websocket = new de.roderick.weberknecht.WebSocketConnection(url);
        websocket.setEventHandler(new de.roderick.weberknecht.WebSocketEventHandler() {
            public void onOpen() {
                opened.set(true);
            }

            public void onMessage(de.roderick.weberknecht.WebSocketMessage message) {
                messagesReceived.add(message.getText());
                messageArrived.countDown();
            }

            public void onClose() {
                closed.set(true);
            }
        });

        websocket.connect(); // Establish WebSocket Connection
        websocket.send("hello world"); // Send UTF-8 Text
        
        // block until message has arrived
        messageArrived.await(500, TimeUnit.MILLISECONDS);

        assertThat(opened.get(), is(true));
        assertThat(messagesReceived, equalTo(asList("HELLO WORLD")));
        
        websocket.close();
        webSocketCloseLatch.await();
        assertThat(closed.get(), is(true));
    }
}
