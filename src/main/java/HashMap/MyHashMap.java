package HashMap;

import java.util.*;

/**
 * Class contains two generic's parametrs
 * @param <T1>
 * @param <T2>
 * Map interface inmlementation.
 * @autor Lyashov Evgeniy
 * @version 1.2
 */
public class MyHashMap<T1, T2> implements Map<T1, T2> {
    private float loadFactor = 0.75f;
    private static int arrayMaxSize = 16;
    private int size = 0;

    /** Class pair realize a hashmap element
     * element consist of key , value, hashcode and link to next elelent the hashmap
     * @param <T1>
     * @param <T2>
     */
    private static class Pair<T1, T2> {
        private final T1 key;
        private final T2 value;
        private int hashCode;
        Pair<T1, T2> next;

        /**
         * Pair constructor
         * @param key
         * @param value
         * @param sizeForHash
         */
        private Pair(T1 key, T2 value, int sizeForHash) {
            this.key = key;
            this.value = value;
            this.hashCode = keyHashCode(key.hashCode(), sizeForHash);
        }

        /**
         * Generane hash kode for key
         * @param hashCode
         * @param sizeForHash
         * @return
         */
        private static int keyHashCode(int hashCode, int sizeForHash) {
            return Math.abs(hashCode) % (sizeForHash - 1);
        }

        /**
         *
         * @return hashmap's key element
         */
        private T1 getKey() {
            return key;
        }

        /**
         *
         * @return hashmap's value element
         */
        private T2 getValue() {
            return value;
        }
    }

    private Pair<T1, T2>[] arr = new Pair[arrayMaxSize];

    /**
     * Put element in hash map
     * if key = null create NullPPointerException
     * @param key
     * @param value
     * @return
     */
    public T2 put(T1 key, T2 value) {
        if (key == null) throw new NullPointerException();
        return putKeyValue(key, value, false);
    }

    /**
     * Get child for pair
     * @param pair
     * @return
     */
    private Pair<T1, T2> getChild(Pair<T1, T2> pair) {
        return pair.next;
    }

    private boolean haveCollision(Pair pair, int index){
        Pair<T1, T2> child;
        while ((child = getChild(pair)) != null) {
            if (!pair.getKey().equals(arr[index].getKey())) {
                return true;
            }
            pair = child;
        }
            return false;
    }


        /**
     * private method, that does put key, value into hashmap
     * flag isResaize is needed for resize hashmap
     * @param key
     * @param value
     * @param isResize
     * @return
     */
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
        else if (haveCollision(pair, index)){
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

    /**
     * Resize hashmap
     * @param newCapacity
     */
    private void resize(int newCapacity) {
        Pair[] newTable = new Pair[newCapacity];
        Pair[] tmpTable = Arrays.copyOf(arr, arr.length);
        arr = newTable;
        for (Pair pair : tmpTable) {
            if (pair == null) continue;
            putKeyValue((T1) pair.getKey(), (T2) pair.getValue(), true);
            Pair<T1, T2> child;
            while ((child = getChild(pair)) != null) {
                Pair<T1, T2> parent = pair;
                putKeyValue((T1) pair.getKey(), (T2) pair.getValue(), true);
                this.size--;
                pair = child;
            }

        }
    }

    /**
     * Check hashmap is empty
     * @return is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Contains ke
     * @param key
     * @return
     */
    public boolean containsKey(Object key) {
         return get(key) != null ? true : false;
    }


    /**
     * Get hashmap's key
     * @param key
     * @return
     */
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

    /**
     * Remove hashmap's element by key
     * @param key
     * @return
     */
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

    /**
     * @return hashmap's size
     */
    public int size() {
        return this.size;
    }

    /**
     * Override method Set
     * @return
     */
    @Override
    public Set<Entry<T1, T2>> entrySet() {
        HashMap<T1, T2> hashMap = new HashMap<>();
        hashMap = fillHashMap();
        return hashMap.entrySet();
    }

    /**
     * Override method containsValue
     * @param value
     * @return
     */
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

    /**
     * Override method putAll
     * @param m
     */
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

    /**
     * Fill hashmap from arr
     * @return hashmap
     */
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

    /**
     * Override method Set
     * @return
     */
    @Override
    public Set<T1> keySet() {
        HashMap<T1, T2> hashMap = fillHashMap();
        return hashMap.keySet();
    }

    /**
     * Override collection
     * @return
     */
    @Override
    public Collection<T2> values() {
        HashMap<T1, T2> hashMap = fillHashMap();
        return hashMap.values();
    }
}
