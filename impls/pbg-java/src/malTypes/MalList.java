package malTypes;

import java.util.ArrayList;
import java.util.List;

public class MalList extends MalType {
    List<MalType> list = new ArrayList<>();
    public void add(MalType m) {
        this.list.add(m);
    }
    public List<MalType> getList() {
        return list;
    }
}
