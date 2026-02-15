package malTypes;

public class MalInteger extends MalType {
    private int number;
    public MalInteger(int num) {
        this.number = num;
    }
    public int getNumber() {
        return number;
    }
}
