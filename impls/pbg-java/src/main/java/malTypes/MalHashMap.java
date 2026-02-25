package main.java.malTypes;

import java.util.HashMap;

public class MalHashMap extends MalCollectionType {
    public static final String HASHMAP_START = "{";
    public static final String HASHMAP_END = "}";
    private HashMap<MalHashMapKey, MalType> hashMap;
    private int counter;
    private MalHashMapKey key;
    public MalHashMap() {
        this.hashMap = new HashMap<>();
        this.counter = 0;
    }
    public HashMap<MalHashMapKey, MalType> getCollection() {
        return this.hashMap;
    }
    @Override
    public void add(MalType m) {
        if (counter % 2 == 0) {
            this.key = (MalHashMapKey) m;
        } else {
            this.put(this.key, m);
        }
        this.counter++;
    }
    public String getStart() {
        return HASHMAP_START;
    }
    public String getEnd() {
        return HASHMAP_END;
    }
    public void put(MalHashMapKey key, MalType value) {
        hashMap.put(key, value);
    }
    @Override
    public String toString(boolean printReadably) {
        StringBuilder b = new StringBuilder();
        b.append(this.getStart());
        for (MalHashMapKey key : this.hashMap.keySet()) {
            b.append(key.toString(printReadably) + " " + this.hashMap.get(key).toString(printReadably) + " ");
        }
        if (b.length() > this.getStart().length()) {
            b.deleteCharAt(b.length() - 1);
        }
        b.append(this.getEnd());
        return b.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(this.getClass().equals(o.getClass()))) return false;
        return ((MalHashMap) o).getCollection().equals(this.getCollection());
    }
}
