package main.java.malTypes;

public abstract class MalBoolean extends MalType {
    public static MalBoolean getBoolean(boolean b) {
        if (b) return new MalTrue();
        return new MalFalse();
    }
}
