package swoop.route;

import java.util.Collections;
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
        if(underlying==null)
            return null;
        if(name.startsWith(":"))
            return underlying.first(name.substring(1));
        else
            return underlying.first(name);
    }
    
    public List<String> routeParams(String name) {
        if(underlying==null)
            return Collections.emptyList();
        if(name.startsWith(":"))
            return underlying.get(name.substring(1));
        else
            return underlying.get(name);
    }
    
    public Set<String> routeParamKeys() {
        if(underlying==null)
            return Collections.emptySet();
        return underlying.keySet();
    }
}
