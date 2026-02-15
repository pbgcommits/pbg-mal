package malTypes;

public class MalNil extends MalType {
    public final static String NIL = "nil";
    @Override
    public String toString() {
        return MalNil.NIL;
    }
}
