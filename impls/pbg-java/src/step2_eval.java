import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import malTypes.MalFunction;
import malTypes.MalHashMap;
import malTypes.MalHashMapKey;
import malTypes.MalList;
import malTypes.MalSymbol;
import malTypes.MalType;
import malTypes.MalVector;

public class step2_eval {
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        step2_eval repl = new step2_eval();
        while (true) {
            try {
                System.out.print("user> ");
                String input = s.nextLine();
                String output = repl.repl(input, new ReplEnv());
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

    
    public String repl(String s, ReplEnv repl_env) throws Exception {
        return print(eval(read(s), repl_env));
    }
    
    public MalType read(String s) throws Exception {
        return Reader.readStr(s);
    }
    public MalType eval(MalType ast, ReplEnv repl_env) throws Exception {
        if (ast instanceof MalSymbol) {
            if (!repl_env.getMap().containsKey(ast.toString())) {
                throw new Exception("not found");
            }
            return repl_env.getMap().get(ast.toString());
        } else if (ast instanceof MalList) {
            List<MalType> originalList = ((MalList) ast).getCollection();
            if (originalList.size() == 0) {
                return ast;
            }
            List<MalType> list = new ArrayList<>();
            for (MalType m : originalList) {
                list.add(this.eval(m, repl_env));
            }
            if (!(list.get(0) instanceof MalFunction)) {
                throw new Exception("Invalid list format");
            }
            MalFunction first = (MalFunction) list.get(0);
            return first.operate(list.subList(1, list.size()).toArray(new MalType[0]));
        } else if (ast instanceof MalVector) {
            MalVector ov = (MalVector) ast;
            MalVector v = new MalVector();
            for (MalType m : ov.getCollection()) {
                v.add(this.eval(m, repl_env));
            }
            return v;
        } else if (ast instanceof MalHashMap) {
            MalHashMap og = (MalHashMap) ast;
            MalHashMap map = new MalHashMap();
            for (MalHashMapKey k : og.getCollection().keySet()) {
                map.put(k, this.eval(og.getCollection().get(k), repl_env));
            }
            return map;
        } else {
            return ast;
        }
    }
    public String print(MalType exp) {
        return Printer.pr_str(exp);
    }
}
