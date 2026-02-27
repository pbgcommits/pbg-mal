package main.java.malTypes;

import main.java.Env;

public class MalFunctionWrapper extends MalType {
    private MalFunction fn;
    private MalType ast;
    private MalCollectionListType params;
    private Env env;
    public MalFunctionWrapper(MalType ast, MalCollectionListType params, Env env, MalFunction fn) {
        this.fn = fn;
        this.env = env;
        this.ast = ast;
        this.params = params;
    }
    public MalType getAst() {
        return ast;
    }
    public Env getEnv() {
        return env;
    }
    public MalFunction getFn() {
        return fn;
    }
    public MalCollectionListType getParams() {
        return params;
    }
    @Override
    public String toString(boolean printReadably) {
        return fn.toString();
    }
}
