package sms.model;

public enum EnrollmentStatus {
    ENROLLED("Enrolled"),
    COMPLETED("Completed"),
    DROPPED("Dropped");

    private final String label;

    EnrollmentStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
