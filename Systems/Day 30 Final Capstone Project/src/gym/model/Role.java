package gym.model;

/** ADMIN can do everything; STAFF runs the front desk (members, payments, check-ins, bookings). */
public enum Role {
    ADMIN("Administrator"),
    STAFF("Front Desk Staff");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    @Override
    public String toString() { return label; }
}
