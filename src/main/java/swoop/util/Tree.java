package swoop.util;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tree<K,V> {
    
    private Logger logger = LoggerFactory.getLogger(Tree.class);

    private Map<K, Tree<K,V>> children;
    private List<V> values;
    private boolean valuesAccepted;
    private K id;
    
    
    public Tree(K id) {
        this.id = id;
    }
    
    public Tree<K,V> getOrCreateChild(K key) {
        if(children==null)
            children = New.hashMap();
        Tree<K, V> child = children.get(key);
        if(child==null) {
            child = new Tree<K, V>(key);
            children.put(key, child);
        }
        return child;
    }
    
    public void put(K key, V value) {
        getOrCreateChild(key).addValue(value);
    }
    
    public List<V> get(K key) {
        if(children==null)
            return null;
        Tree<K,V> t = children.get(key);
        return t.values;
    }
    
    public void addValue(V value) {
        if(values==null)
            values = New.arrayList();
        else {
            if(!values.isEmpty() && !valuesAccepted) {
                logger.warn("Method 'acceptValues' has not been called yet, before adding an other value <{}> to <{}>", value, values);
            }
        }
        values.add(value);
    }

    public List<V> getValues() {
        return values;
    }
    
    public boolean hasValues() {
        return (values!=null && !values.isEmpty());
    }

    public void acceptValues() {
        valuesAccepted = true;
    }
    
    public String toString() {
        /*
        StringBuilder sb = new StringBuilder();
        toString(0, sb);
        return sb.toString();
        */
        return "[values: " + values + "]{" + children + "}";
    }
    
    String INDENT = "|   ";
    private void toString(int i, StringBuilder sb) {
        times(i, sb, INDENT).append("#").append(id).append(" ");
        
        if(values!=null) {
            times(i, sb, INDENT).append("  ");
            sb.append("values: ");
            sb.append("[");
            for(V v : values)
                sb.append(v).append(", ");
            reduceLength(sb, 2);
            sb.append("]");
        }
        
        sb.append("\n");
        if(children!=null) {
            for(Map.Entry<K, Tree<K,V>> e : children.entrySet()) {
                times(i, sb, INDENT).append("  ");
                sb.append(e.getKey()).append(" -> \n");
                e.getValue().toString(i+1, sb);
                sb.append("\n");
            }
        }
    }

    private static void reduceLength(StringBuilder sb, int i) {
        sb.setLength(sb.length()-i);
    }

    private static StringBuilder times(int amount, StringBuilder sb, String data) {
        for(int i=0;i<amount;i++)
            sb.append(data);
        return sb;
    }

    public Tree<K,V> sub(K key) {
        if(children!=null)
            return children.get(key);
        return null;
    }
}
