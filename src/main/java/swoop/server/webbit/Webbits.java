package swoop.server.webbit;

import org.webbitserver.HttpRequest;

import swoop.path.Path;
import swoop.path.Verb;
import swoop.util.Net;

public class Webbits {

    public static String query(HttpRequest request) {
        return Net.uriToQuery(request.uri());
    }
    
    public static String pathInfo(HttpRequest request) {
        return Net.uriToPath(request.uri());
    }

    public static Verb method(HttpRequest request) {
        return Verb.lookup(request.method());
    }
    
    public static Path getPath(HttpRequest request) {
        String path = Webbits.pathInfo(request);
        Verb verb = Webbits.method(request);
        return new Path(verb, path);
    }
}
