package main.java.malTypes;

import java.util.List;

public class MalDeref extends MalList {
    public final static String START = "@";
    public MalDeref(MalType t) throws Exception {
        super();
        List<MalType> list = this.getCollection();
        list.add(new MalSymbol("deref"));
        list.add(t);
    }
}
