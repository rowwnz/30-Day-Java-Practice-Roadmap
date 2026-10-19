package sms.model;

public enum AttendanceStatus {
    PRESENT("Present", "P"),
    LATE("Late", "L"),
    ABSENT("Absent", "A"),
    EXCUSED("Excused", "E");

    private final String label;
    private final String code;

    AttendanceStatus(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /** Present and late both count as attended. */
    public boolean countsAsAttended() {
        return this == PRESENT || this == LATE;
    }

    public static AttendanceStatus fromCode(String code) {
        for (AttendanceStatus status : values()) {
            if (status.code.equalsIgnoreCase(code.trim())) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}
