package sms.model;

public enum StudentStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    GRADUATED("Graduated");

    private final String label;

    StudentStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
