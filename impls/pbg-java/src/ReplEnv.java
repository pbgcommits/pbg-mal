import java.util.HashMap;

import malTypes.MalFunction;
import malTypes.MalInteger;
import malTypes.MalType;

public class ReplEnv {
    public final HashMap<String, MalFunction> repl_env = new HashMap<>();
    public HashMap<String, MalFunction> getMap() {
        return repl_env;
    }
    public ReplEnv() {
        repl_env.put("+", new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int sum = 0;
                for (MalType i : a) {
                    sum += ((MalInteger) i).getNumber();
                }
                return new MalInteger(sum);
            }
        });
        repl_env.put("-", new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int diff = 2 * ((MalInteger) a[0]).getNumber();
                for (MalType i : a) {
                    diff -= ((MalInteger) i).getNumber();
                }
                return new MalInteger(diff);
            }
        });
        repl_env.put("*", new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int product = 1;
                for (MalType i : a) {
                    product *= ((MalInteger) i).getNumber();
                }
                return new MalInteger(product);
            }
        });
        repl_env.put("/", new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int quotient = ((MalInteger) a[0]).getNumber() * ((MalInteger) a[0]).getNumber();
                for (MalType i : a) {
                    quotient /= ((MalInteger) i).getNumber();
                }
                return new MalInteger(quotient);
            }
        });
        
    }
}
