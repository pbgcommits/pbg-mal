package main.java.malTypes;

public class MalVector extends MalCollectionListType {
    public static final String VECTOR_START = "[";
    public static final String VECTOR_END = "]";
    @Override
    public String getEnd() {
        return VECTOR_END;
    }
    @Override
    public String getStart() {
        return VECTOR_START;
    }
}
