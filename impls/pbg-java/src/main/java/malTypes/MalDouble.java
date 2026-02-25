package main.java.malTypes;

public class MalDouble extends MalType {
    private final double num;
    public MalDouble(double num) {
        this.num = num;
    }
    public double getNumber() {
        return this.num;
    }
    @Override
    public String toString(boolean printReadably) {
        return String.valueOf(this.num);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(this.getClass().equals(o.getClass()))) return false;
        return this.getNumber() == ((MalDouble) o).getNumber();
    }
}
