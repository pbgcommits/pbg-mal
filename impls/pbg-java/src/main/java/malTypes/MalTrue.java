package main.java.malTypes;

public class MalTrue extends MalBoolean {
    public final static String TRUE = "true";
    @Override
    public String toString(boolean printReadably) {
        return MalTrue.TRUE;
    }
}