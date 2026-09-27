public class Instructor extends Person implements Payable {

    private final String rank;
    private final double ratePerUnit;
    private int unitsHandled;

    public Instructor(String id, String name, String email, String rank, double ratePerUnit) {
        super(id, name, email);
        this.rank = rank;
        this.ratePerUnit = ratePerUnit;
    }

    public String getRank() {
        return rank;
    }

    void addUnits(int units) {
        this.unitsHandled += units;
    }

    @Override
    public double computeMonthlySalary() {
        return unitsHandled * ratePerUnit;
    }

    @Override
    public String describe() {
        return String.format("Instructor[%s] %s (%s) - %d unit(s), salary: PHP %.2f",
                getId(), getName(), rank, unitsHandled, computeMonthlySalary());
    }
}
