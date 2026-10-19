package sms.service;

import sms.exception.EnrollmentException;
import sms.exception.ValidationException;
import sms.model.AttendanceRecord;
import sms.model.AttendanceStatus;
import sms.model.AttendanceSummary;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.Student;
import sms.repository.Repository;
import sms.util.IdGenerator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class AttendanceService {
    private final Repository<AttendanceRecord> records;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final IdGenerator ids;

    public AttendanceService(Repository<AttendanceRecord> records, EnrollmentService enrollmentService,
                             StudentService studentService, CourseService courseService, IdGenerator ids) {
        this.records = records;
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.ids = ids;
    }

    /** Creates the record for that day, or overwrites it if it already exists. */
    public AttendanceRecord mark(String studentId, String courseCode, LocalDate date, AttendanceStatus status) {
        Student student = studentService.getById(studentId);
        Course course = courseService.getById(courseCode);
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException("Attendance cannot be recorded for a future date.");
        }
        Enrollment enrollment = enrollmentService.findActive(student.getId(), course.getCode())
                .orElseThrow(() -> new EnrollmentException(student.getFullName()
                        + " is not actively enrolled in " + course.getCode() + "."));

        List<AttendanceRecord> existing = records.findBy(r -> r.getStudentId().equals(enrollment.getStudentId())
                && r.getCourseCode().equals(enrollment.getCourseCode()) && r.getDate().equals(date));
        if (!existing.isEmpty()) {
            AttendanceRecord record = existing.get(0);
            record.setStatus(status);
            return record;
        }
        AttendanceRecord record = new AttendanceRecord(ids.next("A", 6), student.getId(), course.getCode(),
                date, status);
        records.save(record);
        return record;
    }

    public List<AttendanceRecord> getRecords(String studentId, String courseCode) {
        Student student = studentService.getById(studentId);
        Course course = courseService.getById(courseCode);
        List<AttendanceRecord> list = records.findBy(r -> r.getStudentId().equals(student.getId())
                && r.getCourseCode().equals(course.getCode()));
        list.sort(Comparator.comparing(AttendanceRecord::getDate));
        return list;
    }

    public AttendanceSummary summarize(String studentId, String courseCode) {
        return new AttendanceSummary(getRecords(studentId, courseCode));
    }
}
