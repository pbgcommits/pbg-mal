import java.util.Scanner;

import malTypes.MalType;

public class step1_read_print {
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        step1_read_print repl = new step1_read_print();
        while (true) {
            try {
                System.out.print("user> ");
                String input = s.nextLine();
                String output = repl.repl(input);
                System.out.println(output);
            } 
            catch (java.util.NoSuchElementException e) {
                break;
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println("unbalanced parens");
            } 
            catch (Exception e) {
                // System.out.println(e.getMessage());
                // break;
            }
        }
        s.close();
    }

    public String repl(String s) {
        return print(eval(read(s)));
    }

    public MalType read(String s) {
        return Reader.readStr(s);
    }
    public MalType eval(MalType ast) {
        return ast;
    }
    public String print(MalType exp) {
        return Printer.pr_str(exp);
    }
}