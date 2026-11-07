package gym.model;

public enum MemberType {
    REGULAR("Regular"),
    STUDENT("Student"),
    SENIOR("Senior Citizen");

    private final String label;

    MemberType(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
