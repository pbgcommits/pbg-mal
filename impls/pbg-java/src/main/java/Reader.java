package main.java;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import main.java.malTypes.MalList;
import main.java.malTypes.MalMetadata;
import main.java.malTypes.MalNil;
import main.java.malTypes.MalQuasiQuote;
import main.java.malTypes.MalQuote;
import main.java.malTypes.MalSpliceUnquote;
import main.java.malTypes.MalString;
import main.java.malTypes.MalCollectionType;
import main.java.malTypes.MalDeref;
import main.java.malTypes.MalDouble;
import main.java.malTypes.MalFalse;
import main.java.malTypes.MalHashMap;
import main.java.malTypes.MalInteger;
import main.java.malTypes.MalKeyword;
import main.java.malTypes.MalSymbol;
import main.java.malTypes.MalTrue;
import main.java.malTypes.MalType;
import main.java.malTypes.MalUnquote;
import main.java.malTypes.MalVector;

public class Reader {

    private final static Pattern tokenPattern = Pattern.compile(
        "[\\s,]*(~@|[\\[\\]{}()'`~^@]|\"(?:\\\\.|[^\\\\\"])*\"?|;.*|[^\\s\\[\\]{}('\"`,;)]*)"
    );
    private final static Pattern intPattern = Pattern.compile("-?\\d+");
    private final static Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private final static String COMMENT_START = ";";
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
        for (MatchResult m : list) {
            String token = m.group(1);
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
            case ";":
                return null;
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
            MalType t = this.readForm();
            if (t != null) {
                // Comments return null
                m.add(t);
            }
        }
        this.next();
        return m;
    }
    
    private MalType readAtom() throws Exception {
        String s = this.next();
        if (s.length() == 0) {
            return new MalSymbol(s);
        }
        if (s.startsWith(COMMENT_START)) {
            // If the input is an atom not enclosed in a list
            return null;
        }
        if (s.startsWith(MalQuote.START)) {
            return new MalQuote(this.readForm().toString());
        }
        if (s.startsWith(MalSpliceUnquote.START)) {
            return new MalSpliceUnquote(this.readForm().toString());
        }
        if (s.startsWith(MalUnquote.START)) {
            return new MalUnquote(this.readForm().toString());
        }
        if (s.startsWith(MalQuasiQuote.START)) {
            return new MalQuasiQuote(this.readForm().toString());
        }
        if (s.startsWith(MalDeref.START)) {
            return new MalDeref(this.readForm());
        }
        if (s.startsWith(MalMetadata.START)) {
            MalType metadata = this.readForm();
            MalType data = this.readForm();
            return new MalMetadata(data, metadata);
        }
        if (s.startsWith(MalKeyword.KEYWORD_START)) {
            return new MalKeyword(s);
        }
        if (s.startsWith(MalString.STRING_START)) {
            if (s.length() == 1 || !s.endsWith(MalString.STRING_END)) {
                throw new Exception("unbalanced quotation marks");
            }
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
            return new MalInteger(Integer.parseInt(s));
        }
        Matcher doubleMatcher = doublePattern.matcher(s);
        if (doubleMatcher.matches()) {
            return new MalDouble(Double.parseDouble(s));
        }
        return new MalSymbol(s);
    }
}
