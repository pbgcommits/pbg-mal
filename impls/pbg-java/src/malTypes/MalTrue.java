package malTypes;

public class MalTrue extends MalBoolean {
    public final static String TRUE = "true";
    @Override
    public String toString() {
        return MalTrue.TRUE;
    }
}