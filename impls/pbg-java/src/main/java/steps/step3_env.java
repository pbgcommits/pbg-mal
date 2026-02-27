package main.java.steps;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.Printer;
import main.java.Reader;
import main.java.Env;
import main.java.malTypes.MalCollectionListType;
import main.java.malTypes.MalFunction;
import main.java.malTypes.MalHashMap;
import main.java.malTypes.MalHashMapKey;
import main.java.malTypes.MalInteger;
import main.java.malTypes.MalList;
import main.java.malTypes.MalSymbol;
import main.java.malTypes.MalType;
import main.java.malTypes.MalVector;

public class step3_env {
    private final static String LIST_ERROR = "Invalid list format";
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        step3_env repl = new step3_env();
        Env env = new Env();
        env.set(new MalSymbol("+"), new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int sum = 0;
                for (MalType i : a) {
                    sum += ((MalInteger) i).getNumber();
                }
                return new MalInteger(sum);
            }
        });
        env.set(new MalSymbol("-"), new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int diff = 2 * ((MalInteger) a[0]).getNumber();
                for (MalType i : a) {
                    diff -= ((MalInteger) i).getNumber();
                }
                return new MalInteger(diff);
            }
        });
        env.set(new MalSymbol("*"), new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int product = 1;
                for (MalType i : a) {
                    product *= ((MalInteger) i).getNumber();
                }
                return new MalInteger(product);
            }
        });
        env.set(new MalSymbol("/"), new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int quotient = ((MalInteger) a[0]).getNumber() * ((MalInteger) a[0]).getNumber();
                for (MalType i : a) {
                    quotient /= ((MalInteger) i).getNumber();
                }
                return new MalInteger(quotient);
            }
        });
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
        if (ast instanceof MalSymbol) {
            MalSymbol symbol = (MalSymbol) ast;
            MalType val = env.get(symbol);
            if (val == null) {
                throw new Exception(symbol.toString() + " " + Env.LOOKUP_ERROR);
            }
            return val;
        } else if (ast instanceof MalList) {
            return this.apply(ast, env);
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
    private MalType apply(MalType ast, Env env) throws Exception {
        List<MalType> originalList = ((MalList) ast).getCollection();
        if (originalList.size() == 0) {
            return ast;
        }
        if (originalList.get(0).toString().equals(Env.DEF_ENV_VAR_KW)) {
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
        else if (originalList.get(0).toString().equals(Env.LET_NEW_ENV_KW)) {
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
            return this.eval(originalList.get(2), newEnv);
        }
        List<MalType> list = new ArrayList<>();
        for (MalType m : originalList) {
            list.add(this.eval(m, env));
        }
        if (!(list.get(0) instanceof MalFunction)) {
            throw new Exception(LIST_ERROR);
        }
        MalFunction first = (MalFunction) list.get(0);
        return first.operate(list.subList(1, list.size()).toArray(new MalType[0]));

    }
    public String print(MalType exp) {
        return Printer.pr_str(exp);
    }
}
