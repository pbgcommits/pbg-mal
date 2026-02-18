package malTypes;

public abstract class MalFunction extends MalType {
    public abstract MalType operate(MalType[] a);
    public String toString() {
        return "MalFunction";
    }
}
