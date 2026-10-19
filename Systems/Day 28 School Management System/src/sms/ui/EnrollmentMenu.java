package sms.ui;

import sms.AppContext;
import sms.model.Enrollment;
import sms.service.EnrollmentService;

public class EnrollmentMenu extends Menu {
    private String lastTerm = AppContext.DEFAULT_TERM;

    public EnrollmentMenu(AppContext ctx) {
        super("ENROLLMENT", ctx);
    }

    @Override
    protected String[] options() {
        return new String[]{
                "Enroll student in a course",
                "Drop an enrollment",
                "View a student's enrollments",
                "View a course's enrollments"
        };
    }

    @Override
    protected void handle(int choice) {
        EnrollmentService service = ctx.getEnrollmentService();
        switch (choice) {
            case 1:
                enroll(service);
                break;
            case 2:
                drop(service);
                break;
            case 3:
                Views.enrollments(service.getByStudent(askStudentId()), ctx);
                break;
            case 4:
                Views.enrollments(service.getByCourse(askCourseCode()), ctx);
                break;
            default:
                break;
        }
    }

    private void enroll(EnrollmentService service) {
        String studentId = askStudentId();
        String courseCode = askCourseCode();
        String term = in.readLine("Term [" + lastTerm + "]: ");
        if (!term.isEmpty()) {
            lastTerm = term;
        }
        Enrollment e = service.enroll(studentId, courseCode, lastTerm);
        success("Enrollment " + e.getId() + " created: "
                + ctx.getStudentService().getById(e.getStudentId()).getFullName() + " -> " + e.getCourseCode());
    }

    private void drop(EnrollmentService service) {
        String id = in.readRequired("Enrollment ID: ");
        Enrollment e = service.getById(id);
        String name = ctx.getStudentService().getById(e.getStudentId()).getFullName();
        if (in.readYesNo("Drop " + name + " from " + e.getCourseCode() + "?")) {
            service.drop(e.getId());
            success("Enrollment " + e.getId() + " dropped.");
        } else {
            System.out.println("Cancelled.");
        }
    }
}
