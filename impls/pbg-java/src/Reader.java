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
        Pattern pattern = Pattern.compile(
            "[\\s,]*(~@|[\\[\\]{}()'`~^@]|\"(?:\\\\.|[^\\\\\"])*\"?|;.*|[^\\s\\[\\]{}('\"`,;)]*)"
        );
        Matcher matcher = pattern.matcher(s);
        List<MatchResult> list = matcher.results().collect(Collectors.toList());
        List<String> tokens = new ArrayList<>();
        // System.out.println("input: " + s);
        // System.out.println("Tokens:");
        for (MatchResult m : list) {
            String token = m.group().strip();
            // System.out.println(token);
            tokens.add(token);
        }
        return tokens;
    }

    private MalType readForm() {
        switch (this.peek()) {
            case "(":
                return this.readList();
            default:
                return this.readAtom();
        }
    }

    private MalType readList() {
        MalList m = new MalList();
        this.next();
        while (!this.peek().equals(")")) {
            m.add(this.readForm());
        }
        return m;
    }

    Pattern intPattern = Pattern.compile("-?\\d+");
    Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    
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
