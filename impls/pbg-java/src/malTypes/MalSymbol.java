package malTypes;

public class MalSymbol extends MalType {
    private final String symbol;
    public MalSymbol(String s) {
        this.symbol = s;
    }
    @Override
    public String toString() {
        return this.symbol;
    }
}