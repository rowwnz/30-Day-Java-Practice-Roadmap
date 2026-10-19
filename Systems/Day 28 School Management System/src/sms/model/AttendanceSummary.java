package sms.model;

import java.util.EnumMap;
import java.util.List;
import java.util.OptionalDouble;

/** Read-only tally of attendance records. Excused days are left out of the rate. */
public class AttendanceSummary {
    private final EnumMap<AttendanceStatus, Integer> counts = new EnumMap<>(AttendanceStatus.class);
    private final int total;

    public AttendanceSummary(List<AttendanceRecord> records) {
        for (AttendanceStatus status : AttendanceStatus.values()) {
            counts.put(status, 0);
        }
        for (AttendanceRecord record : records) {
            counts.put(record.getStatus(), counts.get(record.getStatus()) + 1);
        }
        this.total = records.size();
    }

    public int count(AttendanceStatus status) {
        return counts.get(status);
    }

    public int getTotal() {
        return total;
    }

    public OptionalDouble getAttendanceRate() {
        int countable = total - count(AttendanceStatus.EXCUSED);
        if (countable == 0) {
            return OptionalDouble.empty();
        }
        int attended = count(AttendanceStatus.PRESENT) + count(AttendanceStatus.LATE);
        return OptionalDouble.of(attended * 100.0 / countable);
    }
}
