package main.java.malTypes;

public class MalKeyword extends MalHashMapKey {
    public static final String KEYWORD_START = ":";
    private String keyword;
    public MalKeyword(String keyword) {
        this.keyword = keyword;
    }
    @Override
    public String toString(boolean printReadably) {
        return this.keyword;
    }
}
