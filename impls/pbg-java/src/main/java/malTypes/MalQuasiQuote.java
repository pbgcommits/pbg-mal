package main.java.malTypes;

public class MalQuasiQuote extends MalType {
    public final static String START = "`";
    private String string;
    public MalQuasiQuote(String s) {
        this.string = s;
    }
    public String getString() {
        return string;
    }
    @Override
    public String toString(boolean printReadably) {
        return "(quasiquote " + this.string + ")";
    }
}
