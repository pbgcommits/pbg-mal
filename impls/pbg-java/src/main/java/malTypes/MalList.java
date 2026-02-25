package main.java.malTypes;

public class MalList extends MalCollectionListType {
    public static final String LIST_START = "(";
    public static final String LIST_END = ")";
    @Override
    public String getStart() {
        return LIST_START;
    }
    @Override
    public String getEnd() {
        return LIST_END;
    }
}
