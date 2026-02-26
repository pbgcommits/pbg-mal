package main.java.malTypes;

import java.util.List;

public class MalSpliceUnquote extends MalList {
    public final static String START = "~@";
    public final static MalSymbol SYMBOL = new MalSymbol("splice-unquote");
    public MalSpliceUnquote(MalType t) {
        List<MalType> list = getCollection();
        list.add(MalSpliceUnquote.SYMBOL);
        list.add(t);
    }
}
