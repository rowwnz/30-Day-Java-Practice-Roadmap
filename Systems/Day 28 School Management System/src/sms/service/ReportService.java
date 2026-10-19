package sms.service;

import sms.model.AttendanceSummary;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.EnrollmentStatus;
import sms.model.GradeComponent;
import sms.model.GradeScale;
import sms.model.Student;
import sms.model.StudentStatus;
import sms.util.TablePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/** Builds read-only reports. Every method returns a ready-to-print String. */
public class ReportService {
    public static final double HONORS_GWA = 3.5;

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final AttendanceService attendanceService;

    public ReportService(StudentService studentService, TeacherService teacherService, CourseService courseService,
                         EnrollmentService enrollmentService, AttendanceService attendanceService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.attendanceService = attendanceService;
    }

    /** Units-weighted average of NU grade points over completed courses. */
    public OptionalDouble computeGwa(String studentId) {
        double weightedPoints = 0;
        int units = 0;
        for (Enrollment e : enrollmentService.getByStudent(studentId)) {
            Optional<GradeScale> grade = e.getGradeScale();
            if (e.getStatus() == EnrollmentStatus.COMPLETED && grade.isPresent()) {
                int courseUnits = courseService.getById(e.getCourseCode()).getUnits();
                weightedPoints += grade.get().getGradePoint() * courseUnits;
                units += courseUnits;
            }
        }
        return units == 0 ? OptionalDouble.empty() : OptionalDouble.of(weightedPoints / units);
    }

    public String transcript(String studentId) {
        Student student = studentService.getById(studentId);
        StringBuilder sb = new StringBuilder();
        sb.append("TRANSCRIPT OF RECORDS\n\n").append(student.getProfile()).append('\n');

        TablePrinter table = new TablePrinter("Term", "Code", "Course Title", "Units", "Status",
                "Final %", "Grade", "Remarks");
        int unitsEarned = 0;
        for (Enrollment e : enrollmentService.getByStudent(student.getId())) {
            Course course = courseService.getById(e.getCourseCode());
            Optional<GradeScale> grade = e.getGradeScale();
            boolean dropped = e.getStatus() == EnrollmentStatus.DROPPED;
            if (e.isPassed()) {
                unitsEarned += course.getUnits();
            }
            table.addRow(e.getTerm(), course.getCode(), course.getTitle(), course.getUnits(), e.getStatus(),
                    format(e.getFinalGrade()),
                    grade.isPresent() ? grade.get().getLabel() : (dropped ? "Dr" : "-"),
                    grade.isPresent() ? grade.get().getDescription() : (dropped ? "Officially Dropped" : "In Progress"));
        }
        sb.append(table.isEmpty() ? "No enrollment records.\n" : table.render());

        OptionalDouble gwa = computeGwa(student.getId());
        sb.append("\nUnits earned : ").append(unitsEarned).append('\n');
        sb.append("GWA          : ").append(gwa.isPresent() ? String.format("%.2f", gwa.getAsDouble())
                : "N/A (no completed courses)").append('\n');
        return sb.toString();
    }

    public String classRoster(String courseCode) {
        Course course = courseService.getById(courseCode);
        StringBuilder sb = new StringBuilder();
        sb.append("CLASS ROSTER - ").append(course).append('\n');
        sb.append("Teacher  : ").append(courseService.getTeacherName(course)).append('\n');
        sb.append("Units    : ").append(course.getUnits()).append('\n');
        sb.append("Enrolled : ").append(courseService.countActiveEnrollments(course.getCode()))
                .append('/').append(course.getCapacity()).append("\n\n");

        List<String> headers = new ArrayList<>();
        headers.add("Enroll ID");
        headers.add("Student");
        headers.add("Status");
        for (GradeComponent c : GradeComponent.values()) {
            headers.add(c.getShortLabel());
        }
        headers.add("Grade %");
        headers.add("NU");
        headers.add("Attend.");
        TablePrinter table = new TablePrinter(headers.toArray(new String[0]));

        for (Enrollment e : enrollmentService.getByCourse(course.getCode())) {
            Student s = studentService.getById(e.getStudentId());
            List<Object> row = new ArrayList<>();
            row.add(e.getId());
            row.add(s.getFullName());
            row.add(e.getStatus());
            for (GradeComponent c : GradeComponent.values()) {
                row.add(format(e.getGradeSheet().getScore(c)));
            }
            OptionalDouble finalGrade = e.getFinalGrade();
            row.add(finalGrade.isPresent() ? format(finalGrade)
                    : format(e.getGradeSheet().getRunningGrade()) + (e.getGradeSheet().getRunningGrade().isPresent() ? "*" : ""));
            row.add(e.getGradeScale().map(GradeScale::getLabel).orElse(e.getStatus() == EnrollmentStatus.DROPPED ? "Dr" : "-"));
            AttendanceSummary summary = attendanceService.summarize(s.getId(), course.getCode());
            row.add(summary.getAttendanceRate().isPresent()
                    ? String.format("%.0f%%", summary.getAttendanceRate().getAsDouble()) : "-");
            table.addRow(row.toArray());
        }
        sb.append(table.isEmpty() ? "No students enrolled.\n" : table.render());
        sb.append("* running grade (not all components recorded yet)\n");
        return sb.toString();
    }

    public String studentRanking(int limit) {
        List<Object[]> ranked = new ArrayList<>();
        for (Student s : studentService.getAll()) {
            OptionalDouble gwa = computeGwa(s.getId());
            if (gwa.isPresent()) {
                ranked.add(new Object[]{s, gwa.getAsDouble()});
            }
        }
        ranked.sort((a, b) -> Double.compare((Double) b[1], (Double) a[1]));

        TablePrinter table = new TablePrinter("Rank", "ID", "Name", "Program", "Year", "GWA", "Distinction");
        int rank = 0;
        for (Object[] entry : ranked) {
            if (rank >= limit) {
                break;
            }
            Student s = (Student) entry[0];
            double gwa = (Double) entry[1];
            boolean noFailures = enrollmentService.getByStudent(s.getId()).stream()
                    .noneMatch(e -> e.getGradeScale().map(g -> !g.isPassing()).orElse(false));
            String distinction = gwa >= HONORS_GWA && noFailures ? "With Honors" : "-";
            table.addRow(++rank, s.getId(), s.getFullName(), s.getProgram(), s.getYearLevel(),
                    String.format("%.2f", gwa), distinction);
        }
        return "STUDENT RANKING BY GWA\n"
                + (table.isEmpty() ? "No students with completed courses yet.\n" : table.render())
                + "With Honors = GWA >= " + HONORS_GWA + " and no failing (R) grade.\n";
    }

    public String dashboard() {
        StringBuilder sb = new StringBuilder("SCHOOL DASHBOARD\n\n");
        long active = studentService.getAll().stream().filter(s -> s.getStatus() == StudentStatus.ACTIVE).count();
        sb.append(String.format("Students    : %d total, %d active%n", studentService.count(), active));
        sb.append(String.format("Teachers    : %d%n", teacherService.count()));
        sb.append(String.format("Courses     : %d%n", courseService.count()));
        sb.append(String.format("Enrollments : %d enrolled, %d completed, %d dropped%n%n",
                enrollmentService.countByStatus(EnrollmentStatus.ENROLLED),
                enrollmentService.countByStatus(EnrollmentStatus.COMPLETED),
                enrollmentService.countByStatus(EnrollmentStatus.DROPPED)));

        TablePrinter table = new TablePrinter("Code", "Title", "Teacher", "Enrolled", "Load");
        for (Course c : courseService.getAll()) {
            int count = courseService.countActiveEnrollments(c.getCode());
            int percent = (int) Math.round(count * 100.0 / c.getCapacity());
            table.addRow(c.getCode(), c.getTitle(), courseService.getTeacherName(c),
                    count + "/" + c.getCapacity(), percent + "%" + (percent >= 90 ? " (nearly full)" : ""));
        }
        sb.append(table.isEmpty() ? "No courses yet.\n" : table.render());
        return sb.toString();
    }

    private static String format(OptionalDouble value) {
        return value.isPresent() ? String.format("%.2f", value.getAsDouble()) : "-";
    }

    private static String format(Double value) {
        return value == null ? "-" : String.format("%.1f", value);
    }
}
