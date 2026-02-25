package main.java.malTypes;

public class MalUnquote extends MalType {
    public final static String START = "~";
    private String string;
    public MalUnquote(String s) {
        this.string = s;
    }
    public String getString() {
        return string;
    }
    @Override
    public String toString(boolean printReadably) {
        return "(unquote " + this.string + ")";
    }
}
