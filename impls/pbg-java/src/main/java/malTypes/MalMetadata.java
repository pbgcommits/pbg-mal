package main.java.malTypes;

public class MalMetadata extends MalType {
    public final static String START = "^";
    private MalType data, metadata;
    public MalMetadata(MalType data, MalType metadata) {
        this.data = data;
        this.metadata = metadata;
    }
    public MalType getData() {
        return data;
    }
    public MalType getMetadata() {
        return metadata;
    }
    @Override
    public String toString(boolean printReadably) {
        return "(with-meta " + this.data.toString() + " " + this.metadata.toString() + ")";
    }
}
