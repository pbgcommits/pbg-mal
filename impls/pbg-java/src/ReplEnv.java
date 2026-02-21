import java.util.HashMap;

import malTypes.MalSymbol;
import malTypes.MalType;

public class ReplEnv {
    private final HashMap<MalSymbol, MalType> data; 
    private ReplEnv outer;
    public ReplEnv() {
        this.data = new HashMap<>();
        this.outer = null;
    }
    public HashMap<MalSymbol, MalType> getMap() {
        return data;
    }
    public void set(MalSymbol key, MalType value) {
        this.data.put(key, value);
    }
    public MalType get(MalSymbol key) {
        if (!this.data.containsKey(key) && this.outer != null) {
            return this.outer.get(key);
        }
        return this.data.get(key);
    }
}
