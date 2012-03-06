package swoop.path;

public class Path {
    public static final String ALL_PATHS = "*";
    public static final char SINGLE_QUOTE = '\'';

    public static Path parse(String route) {
        return new Path(route);
    }
    
    public static String format(Verb verb, String urlExpr) {
        return verb.name() + " '" + urlExpr + "'";
    }
    
    private Verb verb;
    private String pathPattern;
    
    public Path(String route) {
        int singleQuoteBeg = route.indexOf(SINGLE_QUOTE);
        if(singleQuoteBeg<0) {
            this.verb = Verb.lookup(route.trim());
            this.pathPattern = ALL_PATHS;
        }
        else {
            int singleQuoteEnd = route.indexOf(SINGLE_QUOTE, singleQuoteBeg+1);
            if(singleQuoteEnd<0)
                throw new InvalidPathException("Unbalanced quotes for route: <" + route + ">");
            this.verb = Verb.lookup(route.substring(0, singleQuoteBeg).trim());
            this.pathPattern = route.substring(singleQuoteBeg + 1, singleQuoteEnd).trim();
        }
        if(verb==null)
            throw new InvalidPathException("Invalid HTTP method part from route: <" + route + ">");
    }
    
    public Path(Verb verb) {
        this(verb, ALL_PATHS);
    }
    
    public Path(Verb verb, String pathPattern) {
        super();
        if(verb==null)
            throw new IllegalArgumentException("Missing HTTP method part");
        if(pathPattern==null)
            throw new IllegalArgumentException("Missing HTTP path part");
        this.verb = verb;
        this.pathPattern = pathPattern;
    }

    public String getPathPattern() {
        return pathPattern;
    }
    
    public Verb getVerb() {
        return verb;
    }
}
