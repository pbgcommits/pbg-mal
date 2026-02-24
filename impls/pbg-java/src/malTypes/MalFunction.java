package malTypes;

public abstract class MalFunction extends MalType {
    public abstract MalType operate(MalType[] a) throws Exception;
    public String toString() {
        return "#<function>";
    }
}
