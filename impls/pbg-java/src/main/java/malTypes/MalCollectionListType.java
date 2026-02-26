/** Lists and vectors. */
package main.java.malTypes;

import java.util.ArrayList;
import java.util.List;

public abstract class MalCollectionListType extends MalCollectionType {
    private final List<MalType> list;
    public MalCollectionListType() {
        this.list = new ArrayList<>();
    }
    public List<MalType> getCollection() {
        return this.list;
    }
    public int size() {
        return this.list.size();
    }
    public MalType get(int i) {
        return this.list.get(i);
    }
    @Override
    public void add(MalType m) {
        this.list.add(m);
    }
    @Override
    public abstract String getStart();
    @Override
    public abstract String getEnd();
    public String toString(boolean printReadably) {
        String[] stringList = new String[this.list.size()];
        for (int i = 0; i < this.list.size(); i++) {
            stringList[i] = this.list.get(i).toString(printReadably);
        }
        return this.getStart() + String.join(" ", stringList) + this.getEnd();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        // if (!(this.getClass().equals(o.getClass()))) return false;
        if (!(o instanceof MalCollectionListType)) {
            return false;
        }
        return ((MalCollectionListType) o).getCollection().equals(this.getCollection());
    }
}
