package main.java.malTypes;

public abstract class MalFunction extends MalType {
    public abstract MalType operate(MalType[] a) throws Exception;
    private final boolean isMacro;
    public MalFunction() {
        this(false);
    }
    public MalFunction(boolean isMacro) {
        this.isMacro = isMacro;
    }
    public boolean isMacro() {
        return isMacro;
    }
    public String toString(boolean printReadably) {
        return "#<function>";
    }
}
