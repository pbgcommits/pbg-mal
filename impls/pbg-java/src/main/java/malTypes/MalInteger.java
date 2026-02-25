package main.java.malTypes;

public class MalInteger extends MalType {
    private final int number;
    public MalInteger(int num) {
        this.number = num;
    }
    public int getNumber() {
        return this.number;
    }
    @Override
    public String toString(boolean printReadably) {
        return String.valueOf(this.number);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(this.getClass().equals(o.getClass()))) return false;
        return this.getNumber() == ((MalInteger) o).getNumber();
    }
}
