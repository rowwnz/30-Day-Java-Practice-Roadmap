package gym.model;

/** Membership plans. Edit the prices here to match the gym's rate card. */
public enum Plan {
    DAY_PASS("Day Pass", 1, 150.00),
    MONTHLY("Monthly", 30, 1_500.00),
    QUARTERLY("Quarterly", 90, 4_000.00),
    ANNUAL("Annual", 365, 14_000.00);

    private final String label;
    private final int days;
    private final double price;

    Plan(String label, int days, double price) {
        this.label = label;
        this.days = days;
        this.price = price;
    }

    public String getLabel() { return label; }
    public int getDays() { return days; }
    public double getPrice() { return price; }

    @Override
    public String toString() { return label; }
}
