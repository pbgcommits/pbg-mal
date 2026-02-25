package main.java;
import main.java.malTypes.MalType;

public class Printer {
    public static String pr_str(MalType m) {
        return pr_str(m, true);
    }
    public static String pr_str(MalType m, boolean printReadably) {
        if (m == null) {
            return "";
        }
        return m.toString(printReadably);
    }
}
