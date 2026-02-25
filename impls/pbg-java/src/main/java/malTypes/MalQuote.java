package main.java.malTypes;

public class MalQuote extends MalType {
    public final static String START = "'";
    private String string;
    public MalQuote(String s) {
        this.string = s;
    }
    public String getString() {
        return string;
    }
    @Override
    public String toString(boolean printReadably) {
        return "(quote " + this.string + ")";
    }
}
