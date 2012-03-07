## SwOOp

<img src="https://github.com/Arnauld/swoop/raw/master/doc/images/swoop-logo.png" style="float:right;"/>

Simple Web OOp!

SwOOp is originally a fork from [Spark](https://github.com/perwendel/spark). Idea was to replace JEE Servlet dependency (originally from [Jetty](http://jetty.codehaus.org/jetty/) by a non-blocking and event based HTTP server. After some initial refactorings, this project has emerged as a complete rewriting in order to have a more flexible and easier to test basis. 

After investigation, the underlying HTTP server used will be [Webbit](https://github.com/webbit/webbit) which is based on [Netty](http://www.jboss.org/netty).

## Quick start

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


## Developpers

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

* Spark: [github](https://github.com/perwendel/spark) and [Website](http://www.sparkjava.com/)
* [Sinatra](https://github.com/sinatra/sinatra)
  * [Base code](https://github.com/sinatra/sinatra/blob/master/lib/sinatra/base.rb)
  * [Routing tests](https://github.com/sinatra/sinatra/blob/master/test/routing_test.rb)
  * [Rake parse_query](https://github.com/rack/rack/blob/master/lib/rack/utils.rb#L65)
  * [Rake parse_query tests](https://github.com/rack/rack/blob/master/test/spec_utils.rb#L103)

