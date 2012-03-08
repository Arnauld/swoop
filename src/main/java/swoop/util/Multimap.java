package swoop.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  
 * <strong>This class is not thread safe for concurrent modification.</strong>
 *
 */
public class Multimap<K, V> {

    private Map<K, List<V>> values = New.hashMap();
    
    public Map<K, List<V>> toMap() {
        return values;
    }
    
    public int numberOfKeys() {
        return values.size();
    }
    
    public int numberOfValues() {
        int count = 0;
        for(List<V> val : values.values())
            count += val.size();
        return count;
    }

    public void put(K key, V value) {
        List<V> list = values.get(key);
        if (list == null) {
            list = New.arrayList();
            values.put(key, list);
        }
        list.add(value);
    }
    
    
    public Set<K> keySet() {
        return values.keySet();
    }

    public List<V> get(K key) {
        return values.get(key);
    }

    public V first(K key) {
        List<V> list = values.get(key);
        if (list==null || list.isEmpty())
            return null;
        return list.get(0);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (Map.Entry<K, List<V>> e : values.entrySet()) {
            builder.append(e.getKey()).append("->[");
            
            List<V> list = e.getValue();
            for (V v : list)
                builder.append(v).append(", ");
            if(!list.isEmpty())
                builder.setLength(builder.length()-2);
            builder.append("], ");
        }
        if(!values.isEmpty())
            builder.setLength(builder.length()-2);
        builder.append("}");
        return builder.toString();
    }

}
