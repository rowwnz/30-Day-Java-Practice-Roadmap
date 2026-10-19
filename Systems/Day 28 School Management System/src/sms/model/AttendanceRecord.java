package sms.model;

import java.io.Serializable;
import java.time.LocalDate;

public class AttendanceRecord implements Identifiable, Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String studentId;
    private final String courseCode;
    private final LocalDate date;
    private AttendanceStatus status;

    public AttendanceRecord(String id, String studentId, String courseCode, LocalDate date, AttendanceStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.date = date;
        this.status = status;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}
