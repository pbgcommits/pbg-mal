package malTypes;

public abstract class MalCollectionType extends MalType {
    public abstract void add(MalType m);
    public abstract String getStart();
    public abstract String getEnd();
}
