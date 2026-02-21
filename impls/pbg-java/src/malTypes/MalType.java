package malTypes;
public abstract class MalType {
    public abstract String toString();
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof MalType)) return false;
        return this.toString().equals(b.toString());
    }
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
