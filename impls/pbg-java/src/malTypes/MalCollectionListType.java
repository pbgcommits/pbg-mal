package malTypes;

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
    @Override
    public void add(MalType m) {
        this.list.add(m);
    }
    @Override
    public abstract String getStart();
    @Override
    public abstract String getEnd();
    @Override
    public String toString() {
        String[] stringList = new String[this.list.size()];
        for (int i = 0; i < this.list.size(); i++) {
            stringList[i] = this.list.get(i).toString();
        }
        return this.getStart() + String.join(" ", stringList) + this.getEnd();
    }
}
