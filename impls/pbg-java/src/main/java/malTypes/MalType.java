package main.java.malTypes;
public abstract class MalType {
    @Override
    public String toString() {
        return this.toString(true);
    }
    public abstract String toString(boolean printReadably);
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
