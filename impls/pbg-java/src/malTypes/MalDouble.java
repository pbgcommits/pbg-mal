package malTypes;

public class MalDouble extends MalType {
    private final double num;
    public MalDouble(double num) {
        this.num = num;
    }
    @Override
    public String toString() {
        return String.valueOf(this.num);
    }
}
