package malTypes;

public class MalDouble extends MalType {
    private double num;
    public MalDouble(double num) {
        this.num = num;
    }
    public double getNumber() {
        return num;
    }
}
