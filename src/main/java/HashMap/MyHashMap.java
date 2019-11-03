package HashMap;

import java.util.*;

public class MyHashMap<T1, T2> implements Map<T1, T2> {
    private float loadFactor = 0.75f;
    private static int arrayMaxSize = 16;
    private int size = 0;

    static class Pair<T1, T2> {
        private final T1 key;
        private final T2 value;
        private int hashCode;
        Pair<T1, T2> next;

        private Pair(T1 key, T2 value, int sizeForHash) {
            this.key = key;
            this.value = value;
            this.hashCode = keyHashCode(key.hashCode(), sizeForHash);
        }

        private static int keyHashCode(int hashCode, int sizeForHash) {
            return Math.abs(hashCode) % (sizeForHash - 1);
        }

        private T1 getKey() {
            return key;
        }

        private T2 getValue() {
            return value;
        }
    }

    private Pair<T1, T2>[] arr = new Pair[arrayMaxSize];

    public T2 put(T1 key, T2 value) {
        return putKeyValue(key, value, false);
    }

    private T2 putKeyValue(T1 key, T2 value, boolean isResize) {
        if (arr.length != 0) {
            float persent = (float) size() / (float) arr.length;
            if (persent >= loadFactor) resize(arr.length * 2);
        }
        Pair<T1, T2> pair = new Pair(key, value, arr.length);
        int index = pair.keyHashCode(key.hashCode(), arr.length);
        if (arr[index] == null){
            arr[index] = pair;
            if (!isResize) this.size++;
        }
        else if (!pair.getKey().equals(arr[index].getKey())) {
            //if collision
            pair.next = arr[index];
            arr[index] = pair;
            if (!isResize) this.size++;
        }
        //update element
        else {
            pair.next =  arr[index].next;
            arr[index] = pair;
        }
        return pair.getValue();
    }

    private void resize(int newCapacity) {
        Pair[] newTable = new Pair[newCapacity];
        Pair[] tmpTable = Arrays.copyOf(arr, arr.length);
        arr = newTable;
        for (Pair pair : tmpTable) {
            if (pair == null) continue;
            putKeyValue((T1) pair.getKey(), (T2) pair.getValue(), true);
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(Object key) {
         return get(key) != null ? true : false;
    }

    private Pair<T1, T2> getChild(Pair<T1, T2> pair) {
        return pair.next;
    }

    public T2 get(Object key) {
        int hashCodeKey = Pair.keyHashCode(key.hashCode(), arr.length);
        if (arr[hashCodeKey] == null) return null;
        Pair<T1, T2> pair = arr[hashCodeKey];
        if (key.equals(pair.getKey())) return pair.getValue();
        else {
            //if collision
            Pair<T1, T2> child;
            while ((child = getChild(pair)) != null) {
                if (key.equals(child.getKey()))
                    return child.getValue();
                pair = child;
            }
            return null;
        }
    }

    public T2 remove(Object key) {
        int hashCodeKey = Pair.keyHashCode(key.hashCode(), arr.length);
        Pair<T1, T2> pair = arr[hashCodeKey];
        if (pair == null) throw new NullPointerException();
        if (key.equals(pair.getKey())) {
            //delete pair, move head
            arr[hashCodeKey] = pair.next;
            this.size--;
            return pair.getValue();
        }
        else {
            //if collision
            Pair<T1, T2> child;
            while ((child = getChild(pair)) != null) {
                Pair<T1, T2> parent = pair;
                if (key.equals(child.getKey())){
                    Pair<T1, T2> tmpPair = child;
                    child = null;
                    parent.next = tmpPair.next;
                    this.size--;
                    return tmpPair.getValue();
                }
                pair = child;
            }
            return null;
        }
    }

    public int size() {
        return this.size;
    }

    @Override
    public Set<Entry<T1, T2>> entrySet() {
        HashMap<T1, T2> hashMap = new HashMap<>();
        hashMap = fillHashMap();
        return hashMap.entrySet();
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) continue;
            if (value.equals(arr[i].getValue())){
                return true;
            }
            Pair<T1, T2> pair = arr[i];
            Pair<T1, T2> child;
            while ((child = getChild(pair)) != null) {
                if (value.equals(child.getValue())){
                    return true;
                }
                pair = child;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends T1, ? extends T2> m) {
        for (Map.Entry<?, ?> entry : m.entrySet()) {
            put((T1)entry.getKey(), (T2)entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = null;
        }
        this.size = 0;
    }

    private HashMap fillHashMap(){
        HashMap<T1, T2> hashMap = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            hashMap.put(arr[i].getKey(), arr[i].getValue());
            Pair<T1, T2> pair = arr[i];
            Pair<T1, T2> child;
            while ((child = getChild(pair)) != null) {
                hashMap.put(child.getKey(), child.getValue());
                pair = child;
            }
        }
        return hashMap;
    }
    @Override
    public Set<T1> keySet() {
        HashMap<T1, T2> hashMap = fillHashMap();
        return hashMap.keySet();
    }

    @Override
    public Collection<T2> values() {
        HashMap<T1, T2> hashMap = fillHashMap();
        return hashMap.values();
    }
}
