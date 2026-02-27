package main.java.malTypes;

public class MalMacroFunction extends MalType {
    private MalFunction function;
    public MalMacroFunction(MalFunction f) {
        this.function = f;
    }
    public MalFunction getFunction() {
        return function;
    }
    @Override
    public String toString(boolean printReadably) {
        return "#<macro>";
    }
}
