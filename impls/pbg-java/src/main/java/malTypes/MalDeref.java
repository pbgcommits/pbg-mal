package main.java.malTypes;

public class MalDeref extends MalType {
    public final static String START = "@";
    private String string;
    public MalDeref(String s) {
        this.string = s;
    }
    public String getString() {
        return string;
    }
    @Override
    public String toString(boolean printReadably) {
        return "(deref " + this.string + ")";
    } 
}
