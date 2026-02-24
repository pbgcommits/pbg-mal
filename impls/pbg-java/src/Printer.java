import malTypes.MalString;
import malTypes.MalType;

public class Printer {
    public static String pr_str(MalType m) {
        return pr_str(m, true);
    }
    public static String pr_str(MalType m, boolean printReadably) {
        if (m instanceof MalString) {
            return ((MalString) m).toString(printReadably);
        }
        return m.toString();
    }
}
