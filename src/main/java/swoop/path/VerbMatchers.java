package swoop.path;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.path.Verb.Category;
import swoop.util.New;

public class VerbMatchers {
    
    private static Logger logger = LoggerFactory.getLogger(VerbMatchers.class);
    
    public static VerbMatcher httpMethods = new VerbMatcher() {
        @Override
        public boolean matches(Verb verb) {
            return verb.isHttpMethod();
        }
        @Override
        public boolean belongsTo(Category category) {
            return category == Category.HttpMethod;
        }
    };
    
    public static VerbMatcher webSocket = new VerbMatcher() {
        @Override
        public boolean matches(Verb verb) {
            return verb.isWebSocket();
        }
        @Override
        public boolean belongsTo(Category category) {
            return category == Category.WebSocket;
        }
    };
    
    public static VerbMatcher eventSource = new VerbMatcher() {
        @Override
        public boolean matches(Verb verb) {
            return verb.isEventSource();
        }
        @Override
        public boolean belongsTo(Category category) {
            return category == Category.EventSource;
        }
    };

    /**
     * @param verbs
     * @return
     */
    public static VerbMatcher on(Verb... verbs) {
        if (verbs.length == 1) {
            Verb verb = verbs[0];
            return thisOne(verb);
        }

        int[] perCategory = new int[Category.values().length];
        final Set<Verb> acceptedVerbs = new HashSet<Verb>();
        for (Verb verb : verbs) {
            perCategory[verb.getCategory().ordinal()]++;
            acceptedVerbs.add(verb);
        }
        
        boolean hasAlready = false;
        for(int i : perCategory) {
            if(i>0) {
                if(hasAlready) {
                    logger.warn("Mixing different categories on the same matcher is strongly discouraged [{}]", Arrays.toString(verbs));
                    break;
                }
                hasAlready = true;
            }
        }
        
        return oneOf(acceptedVerbs);
    }

    private static VerbMatcher thisOne(final Verb acceptedVerb) {
        return new VerbMatcher() {
            @Override
            public boolean matches(Verb verb) {
                return (acceptedVerb == verb);
            }
            @Override
            public boolean belongsTo(Category category) {
                return acceptedVerb.belongsTo(category);
            }
        };
    }

    private static VerbMatcher oneOf(final Set<Verb> acceptedVerbs) {
        return new VerbMatcher() {
            @Override
            public boolean matches(Verb verb) {
                return acceptedVerbs.contains(verb);
            }
            @Override
            public boolean belongsTo(Category category) {
                for(Verb acceptedVerb : acceptedVerbs) {
                    if(acceptedVerb.belongsTo(category))
                        return true;
                }
                return false;
            }
        };
    }

    /**
     * For the sake of simplicity: 
     * <code>any</code> does not belongs to any category, since it matches all verb.
     * 
     * @return
     */
    public static VerbMatcher any() {
        return new VerbMatcher() {
            @Override
            public boolean matches(Verb verb) {
                return true;
            }
            @Override
            public boolean belongsTo(Category category) {
                return true;
            }
        };
    }

    /**
     * e.g.
     * <ul>
     *  <li>Single verb: <code>get</code> or <code>post</code>...</li>
     *  <li>List of verbs (comma separated): <code>get,post</code> or <code>put,delete,post</code>...</li>
     *  <li>Any verbs: <code>any</code> or <code>*</code></li>
     * </ul>
     * 
     * @param verbExpression
     * @return
     */
    public static VerbMatcher fromExpression(String verbExpression) {
        Set<Verb> verbs = New.hashSet();
        for(String frag : verbExpression.split(",")) {
            frag = frag.trim();
            if("*".equals(frag) || "any".equalsIgnoreCase(frag))
                return any();
            else {
                Verb verb = Verb.lookup(frag);
                if(verb==null) {
                    logger.warn("Unknown verb <{}> from expression <{}>", frag, verbExpression);
                }
                else
                    verbs.add(verb);
            }
        }
        return oneOf(verbs);
    }

}
