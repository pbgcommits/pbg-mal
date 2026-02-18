package malTypes;

public class MalInteger extends MalType {
    private final int number;
    public MalInteger(int num) {
        this.number = num;
    }
    public int getNumber() {
        return this.number;
    }
    @Override
    public String toString() {
        return String.valueOf(this.number);
    }
}
