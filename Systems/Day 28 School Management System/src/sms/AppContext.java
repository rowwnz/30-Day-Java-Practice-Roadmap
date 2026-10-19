package sms;

import sms.service.AttendanceService;
import sms.service.CourseService;
import sms.service.EnrollmentService;
import sms.service.ReportService;
import sms.service.StudentService;
import sms.service.TeacherService;
import sms.storage.DataStore;
import sms.util.IdGenerator;
import sms.util.InputHelper;

import java.time.LocalDate;

/**
 * Creates every service once and wires their dependencies (manual dependency injection).
 * Menus receive this object instead of building services themselves.
 */
public class AppContext {
    /** Suggested term when enrolling. Academic year starts in August. */
    public static final String DEFAULT_TERM = defaultTerm();

    private final InputHelper input;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final AttendanceService attendanceService;
    private final ReportService reportService;

    public AppContext(DataStore store, InputHelper input) {
        IdGenerator ids = store.getIdGenerator();
        this.input = input;
        this.teacherService = new TeacherService(store.getTeachers(), store.getCourses(), ids);
        this.studentService = new StudentService(store.getStudents(), store.getEnrollments(), ids);
        this.courseService = new CourseService(store.getCourses(), store.getEnrollments(), teacherService);
        this.enrollmentService = new EnrollmentService(store.getEnrollments(), studentService, courseService, ids);
        this.attendanceService = new AttendanceService(store.getAttendance(), enrollmentService,
                studentService, courseService, ids);
        this.reportService = new ReportService(studentService, teacherService, courseService,
                enrollmentService, attendanceService);
    }

    private static String defaultTerm() {
        LocalDate today = LocalDate.now();
        int startYear = today.getMonthValue() >= 8 ? today.getYear() : today.getYear() - 1;
        return "AY " + startYear + "-" + (startYear + 1) + " T1";
    }

    public InputHelper getInput() {
        return input;
    }

    public StudentService getStudentService() {
        return studentService;
    }

    public TeacherService getTeacherService() {
        return teacherService;
    }

    public CourseService getCourseService() {
        return courseService;
    }

    public EnrollmentService getEnrollmentService() {
        return enrollmentService;
    }

    public AttendanceService getAttendanceService() {
        return attendanceService;
    }

    public ReportService getReportService() {
        return reportService;
    }
}
