package gym.model;

public enum PaymentMethod {
    CASH("Cash"),
    GCASH("GCash"),
    CARD("Debit/Credit Card");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    @Override
    public String toString() { return label; }
}
