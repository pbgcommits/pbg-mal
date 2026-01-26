import java.util.Scanner;

public class step0_repl {
  public static void main(String args[]) {
    Scanner s = new Scanner(System.in);
    step0_repl repl = new step0_repl();
    try {
      while (true) {
        System.out.print("user> ");
        String input = s.nextLine();
        String output = repl.repl(input);
        System.out.println(output);
      }
    } catch (java.util.NoSuchElementException e) {

    }
    s.close();
  }

  public String repl(String s) {
    return print(eval(read(s), new Object()));
  }

  public String read(String s) {
    return s;
  }
  public String eval(String ast, Object env) {
    return ast;
  }
  public String print(String exp) {
    return exp;
  }
}