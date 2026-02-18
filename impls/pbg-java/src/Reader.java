import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import malTypes.MalList;
import malTypes.MalNil;
import malTypes.MalString;
import malTypes.MalCollectionType;
import malTypes.MalDouble;
import malTypes.MalFalse;
import malTypes.MalHashMap;
import malTypes.MalInteger;
import malTypes.MalSymbol;
import malTypes.MalTrue;
import malTypes.MalType;
import malTypes.MalVector;

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
    public static MalType readStr(String s) throws Exception {
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

    private MalType readForm() throws Exception {
        String next = this.peek();
        switch (next) {
            case MalList.LIST_START:
            case MalVector.VECTOR_START:
            case MalHashMap.HASHMAP_START:
                return this.readList(next);
            default:
                return this.readAtom();
        }
    }

    private MalType readList(String type) throws Exception {
        MalCollectionType m;
        if (type.equals(MalVector.VECTOR_START)) {
            m = new MalVector();
        } else if (type.equals(MalHashMap.HASHMAP_START)) {
            m = new MalHashMap();
        } else {
            m = new MalList();
        }
        this.next();
        while (!this.peek().equals(m.getEnd())) {
            m.add(this.readForm());
        }
        this.next();
        return m;
    }
    
    private MalType readAtom() throws Exception {
        String s = this.next();
        // System.out.println("symbol: " + s);
        if (s.length() == 0) {
            return new MalSymbol(s);
        }
        if (s.startsWith(MalString.STRING_START)) {
            if (s.length() == 1 || !s.endsWith(MalString.STRING_END)) {
                throw new Exception("unbalanced quotation marks");
            }
            // System.out.println("string");
            return new MalString(s);
        }
        if (s.equals(MalNil.NIL)) {
            return new MalNil();
        }
        if (s.equals(MalTrue.TRUE)) {
            return new MalTrue();
        }
        if (s.equals(MalFalse.FALSE)) {
            return new MalFalse();
        }
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
