package main.java.malTypes;

public class MalString extends MalHashMapKey {
    public final static String STRING_START = "\"";
    public final static String STRING_END = "\"";
    private final String string;
    private final static String ESCAPE_CHAR_MESSAGE = "unbalanced string";
    // private final static String ESCAPE_CHAR_MESSAGE = "Invalid escape sequence (must be one of \\n, \\\", \\\\)";
    /**
     * This constructor assumes s has s[0] == " and s[s.length-1] == ".
     * @throws Exception If an escape character ('\') isn't followed by a valid escape sequence.
     */
    public MalString(String s) throws Exception {
        this(s, true);
    }
    /**
     * @param s 
     * @param quoted Should be set to true if the string is known to be surrounded by quotation marks.
     * @param hyperEscape Set to true if the string is being read in literally (usually, from a .mal file).
     * @throws Exception
     */
    public MalString(String s, boolean quoted) throws Exception {
        int sLength = s.length();
        StringBuilder builder = new StringBuilder();
        char[] sc = s.toCharArray();
        // [1, sLength-1] to remove enclosing double quotes
        int start = quoted ? 1 : 0;
        int end = quoted ? sLength - 1 : sLength;
        for (int i = start; i < end; i++) {
            if (sc[i] == '\\') {
                if (i + 1 == sLength - 1) {
                    throw new Exception(ESCAPE_CHAR_MESSAGE);
                }
                switch (sc[i+1]) {
                    case 'n' -> builder.append('\n');
                    case '\"' -> builder.append(quoted ? '\"' : "\\\"");
                    case '\\' -> builder.append(quoted ? '\\' : "\\\\");
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
