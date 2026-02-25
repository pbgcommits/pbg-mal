package main.java.malTypes;

public class MalAtom extends MalType {
    private MalType value;
    public MalType getValue() {
        return value;
    }
    public void setValue(MalType value) {
        this.value = value;
    }
    public MalAtom(MalType value) {
        this.value = value;
    }
    @Override
    public String toString(boolean printReadably) {
        return "(atom " + value.toString(printReadably) + ")";
    }
}
