package gym.model;

public enum MembershipStatus {
    NO_PLAN("No plan yet"),
    ACTIVE("Active"),
    EXPIRING("Expiring soon"),
    EXPIRED("Expired");

    private final String label;

    MembershipStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() { return label; }
}
