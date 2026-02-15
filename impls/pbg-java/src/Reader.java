import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import malTypes.MalList;
import malTypes.MalDouble;
import malTypes.MalInteger;
import malTypes.MalSymbol;
import malTypes.MalType;

public class Reader {

    private static Pattern tokenPattern = Pattern.compile(
        "[\\s,]*(~@|[\\[\\]{}()'`~^@]|\"(?:\\\\.|[^\\\\\"])*\"?|;.*|[^\\s\\[\\]{}('\"`,;)]*)"
    );
    private static Pattern intPattern = Pattern.compile("-?\\d+");
    private static Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    private Reader(List<String> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }
    private List<String> tokens;
    private int position;
    private String next() {
        return tokens.get(position++);
    }
    private String peek() {
        return tokens.get(position);
    }
    public static MalType readStr(String s) {
        List<String> tokens = Reader.tokenise(s);
        Reader r = new Reader(tokens);
        MalType m = r.readForm();
        return m;
    }
    public static List<String> tokenise(String s) {
        Matcher matcher = tokenPattern.matcher(s);
        List<MatchResult> list = matcher.results().collect(Collectors.toList());
        List<String> tokens = new ArrayList<>();
        // System.out.println("input: " + s);
        // System.out.println("Tokens:");
        for (MatchResult m : list) {
            String token = m.group(1);
            // System.out.println(token);
            tokens.add(token);
        }
        return tokens;
    }

    private MalType readForm() {
        String next = this.peek();
        switch (next) {
            case MalList.LIST_START:
            case MalList.VECTOR_START:
                return this.readList(next);
            default:
                return this.readAtom();
        }
    }

    private MalType readList(String type) {
        MalList m = new MalList(type);
        this.next();
        while (!this.peek().equals(m.getEnd())) {
            m.add(this.readForm());
        }
        this.next();
        return m;
    }
    
    private MalType readAtom() {
        String s = this.next();
        // System.out.println("symbol: " + s);
        Matcher intMatcher = intPattern.matcher(s);
        if (intMatcher.matches()) {
            // System.out.println("int");
            return new MalInteger(Integer.parseInt(s));
        }
        Matcher doubleMatcher = doublePattern.matcher(s);
        if (doubleMatcher.matches()) {
            // System.out.println("double");
            return new MalDouble(Double.parseDouble(s));
        }
        // System.out.println("symbol");
        return new MalSymbol(s);
    }
}
