package main.java.malTypes;

public class MalString extends MalHashMapKey {
    public final static String STRING_START = "\"";
    public final static String STRING_END = "\"";
    private final String string;
    public MalString(String s) {
        this.string = s;
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
