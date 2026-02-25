package main.java.malTypes;

public class MalSymbol extends MalHashMapKey {
    private final String symbol;
    public MalSymbol(String s) {
        this.symbol = s;
    }
    @Override
    public String toString(boolean printReadably) {
        return this.symbol;
    }
}