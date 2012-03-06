package swoop.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public class New {

    public static <K,V> HashMap<K, V> hashMap() {
        return new HashMap<K,V> ();
    }

    public static <V> ArrayList<V> arrayList() {
        return new ArrayList<V>();
    }

    public static <V> List<V> arrayList(int initialCapacity) {
        return new ArrayList<V>(initialCapacity);
    }

    public static <K,V>  Multimap<K, V> multiMap() {
        return new Multimap<K, V>();
    }

    public static <T> CopyOnWriteArraySet<T> copyOnWriteArraySet() {
        return new CopyOnWriteArraySet<T>();
    }

}
