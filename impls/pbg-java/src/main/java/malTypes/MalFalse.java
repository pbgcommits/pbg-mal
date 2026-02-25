package main.java.malTypes;

public class MalFalse extends MalBoolean {
    public final static String FALSE = "false";
    @Override
    public String toString(boolean printReadably) {
        return MalFalse.FALSE;
    }
}
