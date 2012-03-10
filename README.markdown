<img src="https://github.com/Arnauld/swoop/raw/master/doc/images/swoop-logo.png"/>

*Simple Web OOp!*

# Quick start

```java
import static swoop.Swoop.get;
import swoop.Action;
import swoop.Request;
import swoop.Response;

public class Hello {

    public static void main(String[] args) {
        get(new Action() {
            @Override
            public void handle(Request request, Response response) {
                response.body("<h1>Hello!</h1>");
            }
        });
    }
}
```

Launch the main and view it:

    http://0.0.0.0:4567

# Features

* Simple and extensible
* Sinatra based routing ([Sinatra Route](http://www.sinatrarb.com/intro.html#Routes))
  * Route patterns support 
  * Condition support (**in progress**)
* Cookie support
* WebSocket support (**almost done** still need to figure out how to write integration tests on it)
* EventSource support (**not even in progress yet**)
* Static files support
* Pluggable HTTP server
  * Default implementation based on an *event-driven* and *non-blocking* http server ([Webbit](https://github.com/webbit/webbit))


# *SwOOp* in...

## ...two minutes!

Define a filter that mesure the time spent when handling request to any `/hello/` sub routes. And define two handlers on `Get`: 

* one on `/time` route that simply returns the current time
* the other one on `/hello/:name` that extracts the `name` parameter from the called uri, simulates some random job and returns a pretty nice greeting!
* The second route match the filter, so its content will be modified with the time spent

```java
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
                    body += "<br/><small>Request " + request.logInfo() 
                                + " executed in " + (t1-t0) + "ms</small>";
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
                response.body("<h1>Hello " + request.routeParam("name") + "!</h1>");
            }
        });
    }
}
```

Launch the main and view it:

    http://localhost:4567/time

Check now at:

    http://localhost:4567/hello/world
    http://localhost:4567/hello/Everybody

and see the filter that has added the processing duration


## ...less than five minutes! (and with WebSocket)

Let's see how to use and define websocket. First of all the code:

```java
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
    
    /**
     * utility method that simply read a resource and returns its content as string
     */
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
```

Launch the main and view it:

    http://localhost:4567/hello

The route `/hello` simply load the html page from the resource and return it as is. The `Send` button on the html page send the content of the input text through a websocket. The corresponding route is defined on the server at `/hellowebcocket` which simply returns the content of the message in upper case.

Html file `FiveMinutes.html` (copied from [Webbit](https://github.com/webbit/webbit)) is in `src/test/resources`

```html
<html>
  <body>
    <!-- Send text to websocket -->
    <input id="userInput" type="text">
    <button onclick="ws.send(document.getElementById('userInput').value)">Send</button>

    <!-- Results -->
    <div id="message"></div>

    <script>
      function showMessage(text) {
        document.getElementById('message').innerHTML += "<br/>" + text;
      }

      // Have a look to www.modernizr.com as an efficient library to figure out
      // if your browser support webSocket, and other html5 features
      var ws = new WebSocket('ws://' + document.location.host + '/hellowebsocket');
      showMessage('Connecting...');
      ws.onopen = function() { showMessage('Connected!'); };
      ws.onclose = function() { showMessage('Lost connection'); };
      ws.onmessage = function(msg) { showMessage(msg.data); };
    </script>
  </body>
</html>
```

## ... in ten minutes (but event-driven!)

**In progress**

Port of [NodeJS and Callbacks](http://tapestryjava.blogspot.com/2012/03/nodejs-and-callbacks.html) article using SwOOp.

# Contributing

```bash
    $ mvn clean test
```

Integration/Functional<sup>1</sup> tests:

```bash
    $ mvn clean test -Pfunc
```

Performance tests:

```bash
    $ mvn clean test -Pperf
```

<sup>1</sup>: Whereas it is really debatable, in the case of a middleware library i guess both are strongly related, by the way "don’t worry too much about what you call a test, as long as you are clear on what it does and it does a single thing." &mdash; [The false dichotomy of tests](http://gojko.net/2011/01/12/the-false-dichotomy-of-tests/)

## Inspirations & Credits

*SwOOp* was originally a fork from [Spark](https://github.com/perwendel/spark). Idea was to replace JEE Servlet dependency (originally from [Jetty](http://jetty.codehaus.org/jetty/) by a non-blocking and event based HTTP server. After some initial refactorings, this project has emerged as a complete rewriting in order to have a more flexible and easier to test basis. There are some remainings especially *the static bootstrap initialization*.

After investigation, the by-default underlying HTTP server is [Webbit](https://github.com/webbit/webbit) which is based on [Netty](http://www.jboss.org/netty).

* Spark: [github](https://github.com/perwendel/spark) and [Website](http://www.sparkjava.com/)
* [Webbit](https://github.com/webbit/webbit)
* [Sinatra](https://github.com/sinatra/sinatra)
  * [Base code](https://github.com/sinatra/sinatra/blob/master/lib/sinatra/base.rb)
  * [Routing tests](https://github.com/sinatra/sinatra/blob/master/test/routing_test.rb)
  * [Rake parse_query](https://github.com/rack/rack/blob/master/lib/rack/utils.rb#L65)
  * [Rake parse_query tests](https://github.com/rack/rack/blob/master/test/spec_utils.rb#L103)

