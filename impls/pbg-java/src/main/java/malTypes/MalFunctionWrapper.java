package main.java.malTypes;

import main.java.ReplEnv;

public class MalFunctionWrapper extends MalType {
    private MalFunction fn;
    private MalType ast;
    private MalCollectionListType params;
    private ReplEnv env;
    public MalFunctionWrapper(MalType ast, MalCollectionListType params, ReplEnv env, MalFunction fn) {
        this.fn = fn;
        this.env = env;
        this.ast = ast;
        this.params = params;
    }
    public MalType getAst() {
        return ast;
    }
    public ReplEnv getEnv() {
        return env;
    }
    public MalFunction getFn() {
        return fn;
    }
    public MalCollectionListType getParams() {
        return params;
    }
    @Override
    public String toString() {
        return fn.toString();
    }
}
