package samples.quickstart;

import static swoop.Swoop.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import swoop.*;

public class FiveMinutes {

    public static void main(String[] args) {
        get(new Action("/hello") {
            @Override
            public void handle(Request request, Response response) {
                // for code simplicity page is loaded from a resource
                // use Swoop.staticDir(dir) for static content instead
                response.body(resourceAsString("FiveMinutes.html"));
            }
        });
        webSocket(new WebSocket("/hellowebsocket") {
            @Override
            public void onMessage(WebSocketConnection connection, WebSocketMessage msg) {
                // echo back message in upper case if it is text
                if(msg.isText())
                    connection.send(msg.text().toUpperCase());
            }
        });
    }
    
    private static String resourceAsString(String resourcePath) {
        InputStream input = FiveMinutes.class.getResourceAsStream(resourcePath);
        try {
            return IOUtils.toString(input);
        }
        catch(IOException ioe) {
            throw new SwoopException("Failed to load resource <" + resourcePath + ">", ioe);
        }
        finally {
            IOUtils.closeQuietly(input);
        }
    }
}