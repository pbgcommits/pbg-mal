package main.java.malTypes;

public class MalSpliceUnquote extends MalType {
    public final static String START = "~@";
    private String string;
    public MalSpliceUnquote(String s) {
        this.string = s;
    }
    public String getString() {
        return string;
    }
    @Override
    public String toString(boolean printReadably) {
        return "(splice-unquote " + this.string + ")";
    }
}
