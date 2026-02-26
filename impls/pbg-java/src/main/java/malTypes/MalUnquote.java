package main.java.malTypes;

import java.util.List;

public class MalUnquote extends MalList {
    public final static String START = "~";
    public final static MalSymbol SYMBOL = new MalSymbol("unquote");
    public MalUnquote(MalType t) {
        List<MalType> list = getCollection();
        list.add(MalUnquote.SYMBOL);
        list.add(t);
    }
}
