package main.java.malTypes;

import java.util.List;

public class MalMetadata extends MalList {
    public final static String START = "^";
    public final static MalSymbol SYMBOL = new MalSymbol("with-meta");
    public MalMetadata(MalType data, MalType metadata) {
        List<MalType> list = getCollection();
        list.add(MalMetadata.SYMBOL);
        list.add(data);
        list.add(metadata);
    }
    public MalType getData() {
        return getCollection().get(1);
    }
    public MalType getMetadata() {
        return getCollection().get(2);
    }
}
