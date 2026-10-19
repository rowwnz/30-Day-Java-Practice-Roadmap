package sms.ui;

import sms.AppContext;
import sms.model.AttendanceRecord;
import sms.model.AttendanceStatus;
import sms.model.AttendanceSummary;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.Student;
import sms.service.AttendanceService;
import sms.util.TablePrinter;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;

public class AttendanceMenu extends Menu {

    public AttendanceMenu(AppContext ctx) {
        super("ATTENDANCE", ctx);
    }

    @Override
    protected String[] options() {
        return new String[]{
                "Take class attendance",
                "Mark one student",
                "View a student's attendance in a course"
        };
    }

    @Override
    protected void handle(int choice) {
        AttendanceService service = ctx.getAttendanceService();
        switch (choice) {
            case 1:
                takeClassAttendance(service);
                break;
            case 2:
                markOne(service);
                break;
            case 3:
                viewStudent(service);
                break;
            default:
                break;
        }
    }

    private void takeClassAttendance(AttendanceService service) {
        Course course = ctx.getCourseService().getById(askCourseCode());
        List<Enrollment> list = ctx.getEnrollmentService().getActiveByCourse(course.getCode());
        if (list.isEmpty()) {
            System.out.println("No active students in " + course.getCode() + ".");
            return;
        }
        LocalDate date = in.readDate("Date");
        System.out.println("P = Present, L = Late, A = Absent, E = Excused. Enter = Present.");

        int[] tally = new int[AttendanceStatus.values().length];
        for (Enrollment e : list) {
            Student s = ctx.getStudentService().getById(e.getStudentId());
            AttendanceStatus status;
            while (true) {
                String value = in.readLine(String.format("  %-28s [P]: ", s.getFullName()));
                status = value.isEmpty() ? AttendanceStatus.PRESENT : AttendanceStatus.fromCode(value);
                if (status != null) {
                    break;
                }
                System.out.println("    Use P, L, A or E.");
            }
            service.mark(s.getId(), course.getCode(), date, status);
            tally[status.ordinal()]++;
        }
        StringBuilder summary = new StringBuilder();
        for (AttendanceStatus status : AttendanceStatus.values()) {
            summary.append(status).append(": ").append(tally[status.ordinal()]).append("  ");
        }
        success("Attendance saved for " + course.getCode() + " on " + date + ". " + summary.toString().trim());
    }

    private void markOne(AttendanceService service) {
        String studentId = askStudentId();
        String courseCode = askCourseCode();
        LocalDate date = in.readDate("Date");
        AttendanceStatus status = in.readEnum("Status: ", AttendanceStatus.class);
        service.mark(studentId, courseCode, date, status);
        success("Marked " + status + " on " + date + ".");
    }

    private void viewStudent(AttendanceService service) {
        String studentId = askStudentId();
        String courseCode = askCourseCode();
        List<AttendanceRecord> records = service.getRecords(studentId, courseCode);
        if (records.isEmpty()) {
            System.out.println("No attendance records yet.");
            return;
        }
        TablePrinter table = new TablePrinter("Date", "Day", "Status");
        for (AttendanceRecord r : records) {
            String day = r.getDate().getDayOfWeek().toString();
            table.addRow(r.getDate(), day.charAt(0) + day.substring(1, 3).toLowerCase(), r.getStatus());
        }
        table.print();

        AttendanceSummary summary = service.summarize(studentId, courseCode);
        OptionalDouble rate = summary.getAttendanceRate();
        System.out.printf("Present %d | Late %d | Absent %d | Excused %d | Rate: %s%n",
                summary.count(AttendanceStatus.PRESENT), summary.count(AttendanceStatus.LATE),
                summary.count(AttendanceStatus.ABSENT), summary.count(AttendanceStatus.EXCUSED),
                rate.isPresent() ? String.format("%.1f%%", rate.getAsDouble()) : "N/A");
    }
}
