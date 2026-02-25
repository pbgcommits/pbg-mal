package main.java.malTypes;

public class MalString extends MalHashMapKey {
    public final static String STRING_START = "\"";
    public final static String STRING_END = "\"";
    private final String string;
    private final static String ESCAPE_CHAR_MESSAGE = "unbalanced";
    // private final static String ESCAPE_CHAR_MESSAGE = "Invalid escape sequence (must be one of \\n, \\\", \\\\)";
    public MalString(String s) throws Exception {
        int sLength = s.length();
        StringBuilder builder = new StringBuilder();
        char[] sc = s.toCharArray();
        // [1, sLength-1] to remove enclosing double quotes
        for (int i = 1; i < sLength - 1; i++) {
            if (sc[i] == '\\') {
                if (i + 1 == sLength - 1) {
                    throw new Exception(ESCAPE_CHAR_MESSAGE);
                }
                switch (sc[i+1]) {
                    case 'n' -> builder.append('\n');
                    case '\"' -> builder.append('\"');
                    case '\\' -> builder.append('\\');
                    default -> throw new Exception(ESCAPE_CHAR_MESSAGE);
                }
                i++;
            } else {
                builder.append(sc[i]);
            }
        }
        this.string = builder.toString();
    }

    public String toString(boolean printReadably) {
        if (!printReadably) {
            return this.string;
        }
        return getReadable(this.string);
    }
    
    public static String getReadable(String s) {
        String unescapeBackSlash = s.replace("\\", "\\\\");
        String unescapeQuote = unescapeBackSlash.replace("\"", "\\\"");
        String unescapeNewLine = unescapeQuote.replace("\n", "\\n");
        return "\"" + unescapeNewLine + "\"";
    }
}
