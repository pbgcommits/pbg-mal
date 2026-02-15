package malTypes;

public class MalSymbol extends MalType {
    private String symbol;
    public MalSymbol(String s) {
        this.symbol = s;
    }
    public String getSymbol() {
        return symbol;
    }
}