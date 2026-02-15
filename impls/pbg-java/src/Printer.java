import java.util.ArrayList;
import java.util.List;

import malTypes.MalList;
import malTypes.MalDouble;
import malTypes.MalInteger;
import malTypes.MalSymbol;
import malTypes.MalType;

public class Printer {
    public static String pr_str(MalType m) {
        return m.toString();
    }
}
