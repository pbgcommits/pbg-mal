package main.java.malTypes;

public class MalNil extends MalType {
    public final static String NIL = "nil";
    @Override
    public String toString(boolean printReadably) {
        return MalNil.NIL;
    }
}
