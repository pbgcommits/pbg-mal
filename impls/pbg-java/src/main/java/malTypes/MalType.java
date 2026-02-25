package main.java.malTypes;
public abstract class MalType {
    public abstract String toString();
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(this.getClass().equals(o.getClass()))) return false;
        return this.toString().equals(o.toString());
    }
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
