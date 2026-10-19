package sms.ui;

import sms.AppContext;
import sms.model.Student;
import sms.model.StudentStatus;
import sms.service.StudentService;

import java.util.OptionalDouble;

public class StudentMenu extends Menu {

    public StudentMenu(AppContext ctx) {
        super("STUDENT MANAGEMENT", ctx);
    }

    @Override
    protected String[] options() {
        return new String[]{
                "Register student",
                "List all students",
                "Search students",
                "View student profile",
                "Update student",
                "Change student status",
                "Delete student"
        };
    }

    @Override
    protected void handle(int choice) {
        StudentService service = ctx.getStudentService();
        switch (choice) {
            case 1:
                register(service);
                break;
            case 2:
                Views.students(service.getAll());
                break;
            case 3:
                Views.students(service.search(in.readRequired("Name or ID keyword: ")));
                break;
            case 4:
                viewProfile(service);
                break;
            case 5:
                update(service);
                break;
            case 6:
                changeStatus(service);
                break;
            case 7:
                delete(service);
                break;
            default:
                break;
        }
    }

    private void register(StudentService service) {
        String first = in.readRequired("First name: ");
        String last = in.readRequired("Last name: ");
        String email = in.readRequired("Email: ");
        String contact = in.readRequired("Contact number: ");
        String program = in.readRequired("Program (e.g. BSCS): ");
        int year = in.readInt("Year level (1-5): ", 1, 5);
        Student student = service.register(first, last, email, contact, program, year);
        success("Registered " + student);
    }

    private void viewProfile(StudentService service) {
        Student student = service.getById(askStudentId());
        System.out.print(student.getProfile());
        System.out.println("\nEnrollments:");
        Views.enrollments(ctx.getEnrollmentService().getByStudent(student.getId()), ctx);
        OptionalDouble gwa = ctx.getReportService().computeGwa(student.getId());
        System.out.println("GWA: " + (gwa.isPresent() ? String.format("%.2f", gwa.getAsDouble()) : "N/A"));
    }

    private void update(StudentService service) {
        Student s = service.getById(askStudentId());
        System.out.println("Press Enter to keep the current value.");
        Student updated = service.update(s.getId(),
                in.readOptional("First name", s.getFirstName()),
                in.readOptional("Last name", s.getLastName()),
                in.readOptional("Email", s.getEmail()),
                in.readOptional("Contact number", s.getContactNumber()),
                in.readOptional("Program", s.getProgram()),
                in.readOptionalInt("Year level", s.getYearLevel(), 1, 5));
        success("Updated " + updated);
    }

    private void changeStatus(StudentService service) {
        Student s = service.getById(askStudentId());
        System.out.println("Current status: " + s.getStatus());
        StudentStatus status = in.readEnum("New status: ", StudentStatus.class);
        service.changeStatus(s.getId(), status);
        success(s.getFullName() + " is now " + status + ".");
    }

    private void delete(StudentService service) {
        Student s = service.getById(askStudentId());
        if (in.readYesNo("Delete " + s + "?")) {
            service.delete(s.getId());
            success("Student deleted.");
        } else {
            System.out.println("Cancelled.");
        }
    }
}
