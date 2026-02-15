import java.util.ArrayList;
import java.util.List;

import malTypes.MalList;
import malTypes.MalDouble;
import malTypes.MalInteger;
import malTypes.MalSymbol;
import malTypes.MalType;

public class Printer {
    public static String pr_str(MalType m) {
        if (m instanceof MalDouble) {
            MalDouble num = (MalDouble) m;
            double d = num.getNumber();
            return String.valueOf(d);
        }
        else if (m instanceof MalInteger) {
            MalInteger num = (MalInteger) m;
            int i = num.getNumber();
            return String.valueOf(i);
        }
        else if (m instanceof MalSymbol) {
            MalSymbol symbol = (MalSymbol) m;
            return symbol.getSymbol();
        } 
        else {
            MalList list = (MalList) m;
            List<String> list_elements = new ArrayList<>();
            for (MalType el : list.getList()) {
                list_elements.add(Printer.pr_str(el));
            }
            return "(" + String.join(" ", list_elements) + ")";
        }
    }
}
