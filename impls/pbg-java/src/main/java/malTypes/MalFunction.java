package main.java.malTypes;

public abstract class MalFunction extends MalType {
    public abstract MalType operate(MalType[] a) throws Exception;
    public String toString(boolean printReadably) {
        return "#<function>";
    }
}
