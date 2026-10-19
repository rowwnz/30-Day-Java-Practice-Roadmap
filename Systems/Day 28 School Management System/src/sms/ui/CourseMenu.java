package sms.ui;

import sms.AppContext;
import sms.model.Course;
import sms.service.CourseService;

public class CourseMenu extends Menu {

    public CourseMenu(AppContext ctx) {
        super("COURSE MANAGEMENT", ctx);
    }

    @Override
    protected String[] options() {
        return new String[]{
                "Create course",
                "List all courses",
                "View class roster",
                "Update course",
                "Assign teacher",
                "Unassign teacher",
                "Delete course"
        };
    }

    @Override
    protected void handle(int choice) {
        CourseService service = ctx.getCourseService();
        switch (choice) {
            case 1:
                create(service);
                break;
            case 2:
                Views.courses(service.getAll(), ctx);
                break;
            case 3:
                System.out.print(ctx.getReportService().classRoster(askCourseCode()));
                break;
            case 4:
                update(service);
                break;
            case 5:
                assignTeacher(service);
                break;
            case 6:
                Course course = service.getById(askCourseCode());
                service.unassignTeacher(course.getCode());
                success("Teacher removed from " + course.getCode() + ".");
                break;
            case 7:
                delete(service);
                break;
            default:
                break;
        }
    }

    private void create(CourseService service) {
        String code = in.readRequired("Course code (e.g. PROG101): ");
        String title = in.readRequired("Title: ");
        int units = in.readInt("Units (1-6): ", 1, 6);
        int capacity = in.readInt("Capacity (1-100): ", 1, 100);
        Course course = service.create(code, title, units, capacity);
        success("Created " + course);
        if (in.readYesNo("Assign a teacher now?")) {
            service.assignTeacher(course.getCode(), askTeacherId());
            success("Teacher assigned: " + service.getTeacherName(course));
        }
    }

    private void update(CourseService service) {
        Course c = service.getById(askCourseCode());
        System.out.println("Press Enter to keep the current value.");
        Course updated = service.update(c.getCode(),
                in.readOptional("Title", c.getTitle()),
                in.readOptionalInt("Units", c.getUnits(), 1, 6),
                in.readOptionalInt("Capacity", c.getCapacity(), 1, 100));
        success("Updated " + updated);
    }

    private void assignTeacher(CourseService service) {
        Course course = service.getById(askCourseCode());
        System.out.println("Current teacher: " + service.getTeacherName(course));
        service.assignTeacher(course.getCode(), askTeacherId());
        success(course.getCode() + " is now handled by " + service.getTeacherName(course) + ".");
    }

    private void delete(CourseService service) {
        Course c = service.getById(askCourseCode());
        if (in.readYesNo("Delete " + c + "?")) {
            service.delete(c.getCode());
            success("Course deleted.");
        } else {
            System.out.println("Cancelled.");
        }
    }
}
