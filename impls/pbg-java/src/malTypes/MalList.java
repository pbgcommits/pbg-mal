package malTypes;

import java.util.ArrayList;
import java.util.List;

public class MalList extends MalType {
    public static final String LIST_START = "(";
    public static final String LIST_END = ")";
    public static final String VECTOR_START = "[";
    public static final String VECTOR_END = "]";

    private final String start;
    private final String end;
    private final List<MalType> list;
    
    public MalList(String type) {
        this.list = new ArrayList<>();
        if (type.equals(LIST_START)) {
            start = LIST_START;
            end = LIST_END;
        }
        else if (type.equals(VECTOR_START)) {
            start = VECTOR_START;
            end = VECTOR_END;
        }
        else {
            start = "";
            end = "";
        }
    }
    public void add(MalType m) {
        this.list.add(m);
    }
    public String getEnd() {
        return this.end;
    }
    @Override
    public String toString() {
        String[] stringList = new String[this.list.size()];
        for (int i = 0; i < this.list.size(); i++) {
            stringList[i] = this.list.get(i).toString();
        }
        return this.start + String.join(" ", stringList) + this.end;
    }
}
