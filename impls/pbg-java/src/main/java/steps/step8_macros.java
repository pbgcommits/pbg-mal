package main.java.steps;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.Core;
import main.java.Printer;
import main.java.Reader;
import main.java.Env;
import main.java.malTypes.MalCollectionListType;
import main.java.malTypes.MalFalse;
import main.java.malTypes.MalFunction;
import main.java.malTypes.MalFunctionWrapper;
import main.java.malTypes.MalHashMap;
import main.java.malTypes.MalHashMapKey;
import main.java.malTypes.MalList;
import main.java.malTypes.MalMacroFunction;
import main.java.malTypes.MalNil;
import main.java.malTypes.MalQuote;
import main.java.malTypes.MalSpliceUnquote;
import main.java.malTypes.MalString;
import main.java.malTypes.MalSymbol;
import main.java.malTypes.MalType;
import main.java.malTypes.MalUnquote;
import main.java.malTypes.MalVector;

public class step8_macros {
    private final static String DO_KW = "do";
    private final static String IF_KW = "if";
    private final static String FN_KW = "fn*";
    private final static String QUOTE_KW = "quote";
    private final static String QUASIQUOTE_KW = "quasiquote";
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        step8_macros repl = new step8_macros();
        Env env = new Env();
        Core core = new Core();
        for (MalSymbol key : core.getNameSpace().keySet()) {
            env.set(key, core.getNameSpace().get(key));
        }
        env.set(new MalSymbol("eval"), new MalFunction() {
            step8_macros x = new step8_macros();
            @Override
            public MalType operate(MalType[] a) throws Exception {
                x.verifyLengthAtLeast(a, 1);
                return x.eval(a[0], env);
            }
        });
        try {
            repl.repl("(def! not (fn* (a) (if a false true)))", env);
            repl.repl(
                "(def! load-file (fn* (f) (eval (read-string (str \"(do \" (slurp f) \"\\nnil)\")))))", 
                env);
            repl.repl(
                "(defmacro! cond (fn* (& xs) (if (> (count xs) 0) (list 'if (first xs) (if (> (count xs) 1) (nth xs 1) (throw \"odd number of forms to cond\")) (cons 'cond (rest (rest xs)))))))", 
                env);
            MalList argv = new MalList();
            for (int i = 1; i < args.length; i++) {
                argv.add(new MalString(args[i], false));
            }
            env.set(new MalSymbol("*ARGV*"), argv);
            if (args.length > 0) {
                repl.repl("(load-file \"" + args[0] + "\")", env);
                s.close();
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\nAborting early.");
            s.close();
            return;
        }
        while (true) {
            try {
                System.out.print("user> ");
                String input = s.nextLine();
                String output = repl.repl(input, env);
                System.out.println(output);
            } 
            catch (java.util.NoSuchElementException e) {
                break;
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println("unbalanced parens");
                // e.printStackTrace();
            } 
            catch (Exception e) {
                System.out.println(e.getMessage());
                // e.printStackTrace();
                // break;
            }
        }
        s.close();
    }

    
    public String repl(String s, Env env) throws Exception {
        return print(eval(read(s), env));
    }
    
    public MalType read(String s) throws Exception {
        return Reader.readStr(s);
    }
    public MalType eval(MalType ast, Env env) throws Exception {
        while (true) {
            MalType debug = env.get(new MalSymbol("DEBUG-EVAL"));
            if (debug != null && !(debug instanceof MalFalse) && !(debug instanceof MalNil)) {
                System.out.println("EVAL: " + Printer.pr_str(ast, true) + ", class: " + ast.getClass());
            }
            if (ast instanceof MalSymbol) {
                MalSymbol symbol = (MalSymbol) ast;
                MalType val = env.get(symbol);
                if (val == null) {
                    throw new Exception(symbol.toString() + " " + Env.LOOKUP_ERROR);
                }
                return val;
            } else if (ast instanceof MalList) {
                List<MalType> originalList = ((MalList) ast).getCollection();
                if (originalList.size() == 0) {
                    return ast;
                }
                MalType firstParam = originalList.get(0);
                switch (firstParam.toString()) {
                    case DO_KW: {
                        for (int i = 1; i < originalList.size() - 1; i++) {
                            this.eval(originalList.get(i), env);
                        }
                        ast = originalList.get(originalList.size() - 1);
                        break;
                    }
                    case IF_KW: {
                        verifyLengthAtLeast(originalList, 3);
                        MalType condition = this.eval(originalList.get(1), env);
                        if (condition instanceof MalNil || condition instanceof MalFalse) {
                            if (originalList.size() < 4) return new MalNil();
                            ast = originalList.get(3);
                        } else {
                            ast = originalList.get(2);
                        }
                        break;
                    }
                    case FN_KW: {
                        /**
                         * Given (fn* (a) (+ 1 a)), we wait for the function to be called,
                         * then create an environment binding a to the called function's parameter,
                         * then evaluate (+ 1 a). E.g. ((fn* (a) (+ 1 a)) 5) => 6.
                         */
                        verifyLengthAtLeast(originalList, 3);
                        if (!(originalList.get(1) instanceof MalCollectionListType)) {
                            throw new Exception("Expected fn params to be list/vector, got " 
                                                + originalList.get(1).getClass());
                        }
                        MalCollectionListType params = (MalCollectionListType) originalList.get(1);
                        final Env dupEnv = env;
                        MalFunction f = new MalFunction() {
                            @Override
                            public MalType operate(MalType[] a) throws Exception {
                                Env newEnv = new Env(dupEnv, params, a);
                                return step8_macros.this.eval(originalList.get(2), newEnv);
                            }
                        };
                        return new MalFunctionWrapper(originalList.get(2), params, env, f);
                    }
                    case QUOTE_KW: {
                        verifyLengthAtLeast(originalList, 2);
                        return originalList.get(1);
                    }
                    case QUASIQUOTE_KW: {
                        verifyLengthAtLeast(originalList, 2);
                        ast = quasiquote(originalList.get(1));
                        break;
                    }
                    case Env.DEF_MACRO_KW: {
                        verifyLengthAtLeast(originalList, 3);
                        if (!(originalList.get(1) instanceof MalSymbol)) {
                            throw new Exception("Macro name should be simple symbol.");
                        }
                        MalType macroValue = this.eval(originalList.get(2), env);
                        MalMacroFunction macro;
                        if (macroValue instanceof MalFunctionWrapper) {
                            macro = new MalMacroFunction(((MalFunctionWrapper) macroValue).getFn());
                        } 
                        else if (macroValue instanceof MalFunction) {
                            macro = new MalMacroFunction((MalFunction) macroValue);
                        } 
                        else {
                            throw new Exception("Expected function for macro; got " 
                            + originalList.get(2).getClass());
                        }
                        env.set((MalSymbol) originalList.get(1), macro);
                        return macro;
                    }
                    case Env.DEF_ENV_VAR_KW: {
                        verifyLengthAtLeast(originalList, 3);
                        if (!(originalList.get(1) instanceof MalSymbol)) {
                            throw new Exception("Expected symbol, got " + originalList.get(1).getClass());
                        }
                        MalType value = this.eval(originalList.get(2), env);
                        env.set((MalSymbol) originalList.get(1), value);
                        return value;
                    }
                    case Env.LET_NEW_ENV_KW: {
                        verifyLengthAtLeast(originalList, 2);
                        if (!(originalList.get(1) instanceof MalCollectionListType)) {
                            throw new Exception("Expected list/vector, got " + originalList.get(1).getClass());
                        }
                        Env newEnv = new Env(env);
                        List<MalType> innerList = ((MalCollectionListType) originalList.get(1)).getCollection();
                        if (innerList.size() % 2 != 0) {
                            throw new Exception("Expected list with even number of items");
                        }
                        for (int i = 0; i < innerList.size(); i += 2) {
                            if (!(innerList.get(i) instanceof MalSymbol)) {
                                throw new Exception("Expected symbol, got " + innerList.get(i));
                            }
                            newEnv.set((MalSymbol) innerList.get(i), this.eval(innerList.get(i+1), newEnv));
                        }
                        ast = originalList.size() > 2 ? originalList.get(2) : new MalNil();
                        env = newEnv;
                        break;
                    }
                    default: {
                        // ---apply---
                        List<MalType> evaluatedArgs = new ArrayList<>();
                        MalType first = this.eval(originalList.get(0), env);
                        for (int i = 1; i < originalList.size(); i++) {
                            evaluatedArgs.add(this.eval(originalList.get(i), env));
                        }
                        if (first instanceof MalMacroFunction) {
                            List<MalType> unevaluatedArgs = originalList.subList(1, originalList.size());
                            ast = ((MalMacroFunction) first).getFunction()
                                                            .operate(unevaluatedArgs.toArray(new MalType[0]));
                        } else if (first instanceof MalFunction) {
                            return ((MalFunction)first).operate(evaluatedArgs.toArray(new MalType[0]));
                        } else if (first instanceof MalFunctionWrapper) {
                            MalFunctionWrapper wrapper = (MalFunctionWrapper) first;
                            ast = wrapper.getAst();
                            env = new Env(wrapper.getEnv(), wrapper.getParams(), evaluatedArgs.toArray(new MalType[0]));
                        } else {
                            throw new Exception("Cannot call " + first + " as a function");
                        }
                        break;
                    }
                }
            } else if (ast instanceof MalVector) {
                MalVector ov = (MalVector) ast;
                MalVector v = new MalVector();
                for (MalType m : ov.getCollection()) {
                    v.add(this.eval(m, env));
                }
                return v;
            } else if (ast instanceof MalHashMap) {
                MalHashMap og = (MalHashMap) ast;
                MalHashMap map = new MalHashMap();
                for (MalHashMapKey k : og.getCollection().keySet()) {
                    map.put(k, this.eval(og.getCollection().get(k), env));
                }
                return map;
            } else {
                return ast;
            }
        }
    }

    public String print(MalType exp) {
        return Printer.pr_str(exp);
    }

    private MalType quasiquote(MalType ast) throws Exception {
        return quasiquote(ast, false);
    }

    /** toList should only be true when wishing to treat a vector as a list. */
    private MalType quasiquote(MalType ast, boolean toList) throws Exception {
        if (ast instanceof MalList || ((ast instanceof MalVector && toList))) {
            List<MalType> list = ((MalCollectionListType) ast).getCollection();
            if (!toList && list.size() >= 1 && list.get(0).equals(MalUnquote.SYMBOL)) {
                verifyLengthAtLeast(list, 2);
                return list.get(1);
            } else {
                MalList newList = new MalList();
                for (int i = list.size() - 1; i >= 0; i--) {
                    MalList newNewList = new MalList();
                    MalType elt = list.get(i);
                    if (elt instanceof MalCollectionListType
                        && (((MalCollectionListType) elt).size() >= 1)
                        && (((MalCollectionListType) elt).get(0).equals(MalSpliceUnquote.SYMBOL))
                    ) {
                        MalCollectionListType eltList = (MalCollectionListType) elt;
                        verifyLengthAtLeast(eltList, i);
                        newNewList.add(new MalSymbol("concat"));
                        newNewList.add(eltList.get(1));
                        newNewList.add(newList);
                    } else {
                        newNewList.add(new MalSymbol("cons"));
                        newNewList.add(quasiquote(elt));
                        newNewList.add(newList);
                    }
                    newList = newNewList;
                }
                return newList;
            }
        } else if (ast instanceof MalVector) {
            MalList list = new MalList();
            list.add(new MalSymbol("vec"));
            list.add(quasiquote(ast, true));
            return list;
        } else if ((ast instanceof MalHashMap) || (ast instanceof MalSymbol)) {
            return new MalQuote(ast);
        } else {
            return ast;
        }
    }
    private void verifyLengthAtLeast(MalCollectionListType list, int length) throws Exception {
        if (list.size() < length) {
            throw new Exception("Expected at least" + length + " arguments; got " + list.size());
        }
    }
    private void verifyLengthAtLeast(List<MalType> list, int length) throws Exception {
        if (list.size() < length) {
            throw new Exception("Expected at least" + length + " arguments; got " + list.size());
        }
    }
    private void verifyLengthAtLeast(MalType[] a, int length) throws Exception {
        if (a.length < length) {
            throw new Exception("Expected at least" + length + " arguments; got " + a.length);
        }
    }
    
}
