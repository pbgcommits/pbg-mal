package main.java.malTypes;

import java.util.List;

public class MalQuasiQuote extends MalList {
    public final static String START = "`";
    public final static MalSymbol SYMBOL = new MalSymbol("quasiquote");
    public MalQuasiQuote(MalType t) {
        List<MalType> list = getCollection();
        list.add(MalQuasiQuote.SYMBOL);
        list.add(t);
    }
}
