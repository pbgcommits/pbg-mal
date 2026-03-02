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
import main.java.malTypes.MalNil;
import main.java.malTypes.MalString;
import main.java.malTypes.MalSymbol;
import main.java.malTypes.MalType;
import main.java.malTypes.MalVector;

public class step6_file {
    private final static String LIST_ERROR = "Invalid list format";
    private final static String DO_KW = "do";
    private final static String IF_KW = "if";
    private final static String FN_KW = "fn*";
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        step6_file repl = new step6_file();
        Env env = new Env();
        Core core = new Core();
        for (MalSymbol key : core.getNameSpace().keySet()) {
            env.set(key, core.getNameSpace().get(key));
        }
        env.set(new MalSymbol("eval"), new MalFunction() {
            step6_file x = new step6_file();
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a.length < 1) {
                    throw new Exception(LIST_ERROR);
                }
                return x.eval(a[0], env);
            }
        });
        try {
            repl.repl("(def! not (fn* (a) (if a false true)))", env);
            repl.repl(
                "(def! load-file (fn* (f) (eval (read-string (str \"(do \" (slurp f) \"\\nnil)\")))))", 
                env);
            MalList argv = new MalList();
            for (int i = 1; i < args.length; i++) {
                argv.add(new MalString(args[i]));
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
                        if (originalList.size() < 2) {
                            throw new Exception(LIST_ERROR);
                        }
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
                        if (originalList.size() != 3 || !(originalList.get(1) instanceof MalCollectionListType)) {
                            throw new Exception(LIST_ERROR);
                        }
                        MalCollectionListType params = (MalCollectionListType) originalList.get(1);
                        final Env dupEnv = env;
                        MalFunction f = new MalFunction() {
                            @Override
                            public MalType operate(MalType[] a) throws Exception {
                                Env newEnv = new Env(dupEnv, params, a);
                                return step6_file.this.eval(originalList.get(2), newEnv);
                            }
                        };
                        return new MalFunctionWrapper(originalList.get(2), params, env, f);
                    }
                    case Env.DEF_ENV_VAR_KW: {
                        if (originalList.size() != 3) {
                            throw new Exception(LIST_ERROR);
                        }
                        if (!(originalList.get(1) instanceof MalSymbol)) {
                            throw new Exception(LIST_ERROR);
                        }
                        MalType value = this.eval(originalList.get(2), env);
                        env.set((MalSymbol) originalList.get(1), value);
                        return value;
                    }
                    case Env.LET_NEW_ENV_KW: {
                        if (originalList.size() != 3) {
                            throw new Exception(LIST_ERROR);
                        }
                        if (!(originalList.get(1) instanceof MalCollectionListType)) {
                            throw new Exception(LIST_ERROR);
                        }
                        Env newEnv = new Env(env);
                        List<MalType> innerList = ((MalCollectionListType) originalList.get(1)).getCollection();
                        if (innerList.size() % 2 != 0) {
                            throw new Exception(LIST_ERROR);
                        }
                        for (int i = 0; i < innerList.size(); i += 2) {
                            if (!(innerList.get(i) instanceof MalSymbol)) {
                                throw new Exception(LIST_ERROR);
                            }
                            newEnv.set((MalSymbol) innerList.get(i), this.eval(innerList.get(i+1), newEnv));
                        }
                        ast = originalList.get(2);
                        env = newEnv;
                        break;
                        // return this.eval(originalList.get(2), newEnv);
                    }
                    default: {
                        // ---apply---
                        List<MalType> list = new ArrayList<>();
                        for (MalType m : originalList) {
                            list.add(this.eval(m, env));
                        }
                        if (list.get(0) instanceof MalFunction) {
                            MalFunction first = (MalFunction) list.get(0);
                            return first.operate(list.subList(1, list.size()).toArray(new MalType[0]));
                        } else if (list.get(0) instanceof MalFunctionWrapper) {
                            MalFunctionWrapper wrapper = (MalFunctionWrapper) list.get(0);
                            ast = wrapper.getAst();
                            Env newEnv = new Env(wrapper.getEnv(), wrapper.getParams(), 
                                list.subList(1, list.size()).toArray(new MalType[0])
                            );
                            env = newEnv;
                        } else {
                            throw new Exception(LIST_ERROR);
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
}
