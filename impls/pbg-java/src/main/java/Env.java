package main.java;
import java.util.HashMap;
import java.util.List;

import main.java.malTypes.MalCollectionListType;
import main.java.malTypes.MalList;
import main.java.malTypes.MalSymbol;
import main.java.malTypes.MalType;

public class Env {
    private final HashMap<MalSymbol, MalType> data = new HashMap<>(); 
    private Env outer = null;
    public final static String DEF_ENV_VAR_KW = "def!";
    public final static String LET_NEW_ENV_KW = "let*";
    public final static String DEF_MACRO_KW = "defmacro!";
    public final static String LOOKUP_ERROR = "not found.";
    public Env() {}
    public Env(Env outer) {
        this.outer = outer;
    }
    public Env(Env outer, MalCollectionListType binds, MalType[] params) throws Exception {
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
