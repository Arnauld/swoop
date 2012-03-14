package swoop.route;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.RouteChain;
import swoop.util.Context;
import swoop.util.Multimap;

public class RouteChainBasic<R extends FilterAware> implements RouteChain {

    public static <R extends FilterAware> RouteChainBasic<R> create(Invoker<RouteMatch<R>> invoker,
            List<RouteMatch<R>> links, Context context) {
        return new RouteChainBasic<R>(invoker, links, context);
    }

    private Logger logger = LoggerFactory.getLogger(RouteChainBasic.class);

    private final Invoker<RouteMatch<R>> invoker;
    private final List<RouteMatch<R>> links;
    private final Context context;
    private int index;


    public RouteChainBasic(Invoker<RouteMatch<R>> invoker, //
                           List<RouteMatch<R>> links, //
                           Context context) {
        super();
        this.invoker = invoker;
        this.links = links;
        this.context = context;
    }
    
    @Override
    public Context context() {
        return context;
    }

    @Override
    public void invokeNext() {
        if (index == links.size()) {
            logger.warn("No more link in the chain. This usually happens when there is no matching target and only filters");
            return;
        }

        UnderlyingModifier underlyingModifier = new UnderlyingModifier(context.adaptTo(RouteParameters.class));
        try {
            RouteMatch<R> routeMatch = links.get(index++);
            underlyingModifier.changeWith(routeMatch.getRouteParameters());
            invoker.invoke(routeMatch, this);
        } finally {
            underlyingModifier.revert();
            index--;
        }
    }
    
    private static class UnderlyingModifier {
        private RouteParameters routeParameters;
        private Multimap<String, String> previous;
        public UnderlyingModifier(RouteParameters routeParameters) {
            this.routeParameters = routeParameters;
        }
        public void changeWith(Multimap<String, String> content) {
            if(routeParameters==null)
                return;
            previous = routeParameters.getUnderlying();
            routeParameters.setUnderlying(content);
        }
        public void revert() {
            if(routeParameters==null)
                return;
            routeParameters.setUnderlying(previous);
        }
        
    }

}