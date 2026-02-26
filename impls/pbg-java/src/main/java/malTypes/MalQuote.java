package main.java.malTypes;

import java.util.List;

public class MalQuote extends MalList {
    public final static String START = "'";
    public final static MalSymbol SYMBOL = new MalSymbol("quote");
    public MalQuote(MalType t) {
        List<MalType> list = getCollection();
        list.add(MalQuote.SYMBOL);
        list.add(t);
    }
}
