package malTypes;

public class MalFalse extends MalBoolean {
    public final static String FALSE = "false";
    @Override
    public String toString() {
        return MalFalse.FALSE;
    }
}
