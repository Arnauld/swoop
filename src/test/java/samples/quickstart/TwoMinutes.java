package samples.quickstart;

import static swoop.Swoop.*;

import java.util.Random;

import swoop.*;

public class TwoMinutes {

    public static void main(String[] args) {
        around(new Filter("/hello/*") {
            @Override
            public void handle(Request request, Response response, RouteChain routeChain) {
                long t0 = System.currentTimeMillis();
                try  {
                    routeChain.invokeNext();
                }
                finally {
                    long t1 = System.currentTimeMillis();
                    String body = response.body();
                    body += "<br/><small>Request " + request.logInfo() + " executed in " + (t1-t0) + "ms</small>";
                    response.body(body);
                }
            }
        });
        get(new Action("/time") {
            @Override
            public void handle(Request request, Response response) {
                response.body("<h1>Current time is: " + new java.util.Date() + "</h1>");
            }
        });
        get(new Action("/hello/:name") {
            @Override
            public void handle(Request request, Response response) {
                try {
                    // simulate some random processing
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) { 
                    /* ignore */
                }
                response.body("<h1>Hello!" + request.routeParam("name") + "</h1>");
            }
        });
    }
}