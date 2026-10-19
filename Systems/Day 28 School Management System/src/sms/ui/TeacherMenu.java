package sms.ui;

import sms.AppContext;
import sms.model.Teacher;
import sms.service.TeacherService;

public class TeacherMenu extends Menu {

    public TeacherMenu(AppContext ctx) {
        super("TEACHER MANAGEMENT", ctx);
    }

    @Override
    protected String[] options() {
        return new String[]{
                "Add teacher",
                "List all teachers",
                "Search teachers",
                "View teacher profile",
                "Update teacher",
                "Delete teacher"
        };
    }

    @Override
    protected void handle(int choice) {
        TeacherService service = ctx.getTeacherService();
        switch (choice) {
            case 1:
                add(service);
                break;
            case 2:
                Views.teachers(service.getAll(), ctx);
                break;
            case 3:
                Views.teachers(service.search(in.readRequired("Name or ID keyword: ")), ctx);
                break;
            case 4:
                viewProfile(service);
                break;
            case 5:
                update(service);
                break;
            case 6:
                delete(service);
                break;
            default:
                break;
        }
    }

    private void add(TeacherService service) {
        String first = in.readRequired("First name: ");
        String last = in.readRequired("Last name: ");
        String email = in.readRequired("Email: ");
        String contact = in.readRequired("Contact number: ");
        String department = in.readRequired("Department: ");
        String specialization = in.readRequired("Specialization: ");
        Teacher teacher = service.hire(first, last, email, contact, department, specialization);
        success("Added " + teacher);
    }

    private void viewProfile(TeacherService service) {
        Teacher teacher = service.getById(askTeacherId());
        System.out.print(teacher.getProfile());
        System.out.println("\nAssigned courses:");
        Views.courses(service.getAssignedCourses(teacher.getId()), ctx);
    }

    private void update(TeacherService service) {
        Teacher t = service.getById(askTeacherId());
        System.out.println("Press Enter to keep the current value.");
        Teacher updated = service.update(t.getId(),
                in.readOptional("First name", t.getFirstName()),
                in.readOptional("Last name", t.getLastName()),
                in.readOptional("Email", t.getEmail()),
                in.readOptional("Contact number", t.getContactNumber()),
                in.readOptional("Department", t.getDepartment()),
                in.readOptional("Specialization", t.getSpecialization()));
        success("Updated " + updated);
    }

    private void delete(TeacherService service) {
        Teacher t = service.getById(askTeacherId());
        if (in.readYesNo("Delete " + t + "?")) {
            service.delete(t.getId());
            success("Teacher deleted.");
        } else {
            System.out.println("Cancelled.");
        }
    }
}
