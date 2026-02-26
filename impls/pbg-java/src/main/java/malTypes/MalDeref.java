package main.java.malTypes;

import java.util.List;

public class MalDeref extends MalList {
    public final static String START = "@";
    public final static MalSymbol SYMBOL = new MalSymbol("deref");
    public MalDeref(MalType t) throws Exception {
        super();
        List<MalType> list = this.getCollection();
        list.add(MalDeref.SYMBOL);
        list.add(t);
    }
}
