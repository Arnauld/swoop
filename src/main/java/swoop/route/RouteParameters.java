package swoop.route;

import java.util.List;
import java.util.Set;

import swoop.util.Multimap;

public class RouteParameters {
    
    private Multimap<String,String> underlying;
    public void setUnderlying(Multimap<String, String> underlying) {
        this.underlying = underlying;
    }
    
    public Multimap<String, String> getUnderlying() {
        return underlying;
    }
    
    public String routeParam(String name) {
        return underlying.first(name);
    }
    
    public List<String> routeParams(String name) {
        return underlying.get(name);
    }
    
    public Set<String> routeParamKeys() {
        return underlying.keySet();
    }
}
