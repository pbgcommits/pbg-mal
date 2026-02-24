import java.util.HashMap;
import java.util.List;

import malTypes.MalCollectionListType;
import malTypes.MalList;
import malTypes.MalSymbol;
import malTypes.MalType;

public class ReplEnv {
    private final HashMap<MalSymbol, MalType> data = new HashMap<>(); 
    private ReplEnv outer = null;
    public final static String DEF_ENV_VAR_KW = "def!";
    public final static String LET_NEW_ENV_KW = "let*";
    public final static String LOOKUP_ERROR = "not found.";
    public ReplEnv() {}
    public ReplEnv(ReplEnv outer) {
        this.outer = outer;
    }
    public ReplEnv(ReplEnv outer, MalCollectionListType binds, MalType[] params) throws Exception {
        this(outer);
        List<MalType> list = binds.getCollection(); 
        for (int i = 0; i < list.size(); i++) {
            MalSymbol symbol = (MalSymbol) list.get(i);
            if (list.get(i).toString().equals("&")) {
                MalList l = new MalList();
                symbol = (MalSymbol) list.get(i+1);
                while (i < params.length) {
                    l.add(params[i++]);
                }
                this.set(symbol, l);
                break;
            }
            this.set(symbol, params[i]);
        }
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
