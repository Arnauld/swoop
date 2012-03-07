package swoop.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CircularBuffer<T> {

    private final Object[] slots;
    private final int capacity;
    private final AtomicInteger index = new AtomicInteger();
    
    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        this.slots = new Object[capacity];
    }
    
    private CircularBuffer(Object[] slots, int index) {
        this.capacity = slots.length;
        this.slots = slots;
        this.index.set(index);
    }
    
    @SuppressWarnings("unchecked")
    public List<T> toList() {
        int idx = index.get();
        int cnt = Math.min(capacity, idx);
        List<T> list = New.arrayList(cnt);
        for(int i=idx-cnt;i<idx;i++) {
            list.add((T)slots[i%capacity]);
        }
        return list;
    }

    public void add(T value) {
        slots[index.getAndIncrement()%capacity] = value;
    }
    
    public int size() {
        return Math.min(capacity, index.get());
    }
    
    public CircularBuffer<T> copy() {
        Object[] copy = new Object[capacity];
        int index = this.index.get();
        for(int i=0, n=Math.min(capacity, index); i<n; i++)
            copy[i] = slots[i];
        return new CircularBuffer<T>(copy, index);
    }
}
