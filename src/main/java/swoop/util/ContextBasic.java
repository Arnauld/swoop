package swoop.util;

import java.util.Map;

public class ContextBasic implements Context {
    private Map<Class<?>,Object> content = New.concurrentHashMap();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T adaptTo(Class<T> type) {
        T value = (T)content.get(type);
        if(value!=null)
            return value;
        for(Object o : content.values())
            if(type.isInstance(o))
                return (T)o;
        return null;
    }
    
    public <T> ContextBasic register(Class<T> type, T value) {
        content.put(type, value);
        return this;
    }

}
