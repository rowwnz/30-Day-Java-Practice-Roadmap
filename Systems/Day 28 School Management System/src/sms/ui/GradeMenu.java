package sms.ui;

import sms.AppContext;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.GradeComponent;
import sms.service.EnrollmentService;

import java.util.List;

public class GradeMenu extends Menu {

    public GradeMenu(AppContext ctx) {
        super("GRADES", ctx);
    }

    @Override
    protected String[] options() {
        return new String[]{
                "Record a score (one enrollment)",
                "Encode a component for a whole class",
                "View grade sheet",
                "View grading system"
        };
    }

    @Override
    protected void handle(int choice) {
        EnrollmentService service = ctx.getEnrollmentService();
        switch (choice) {
            case 1:
                recordOne(service);
                break;
            case 2:
                encodeClass(service);
                break;
            case 3:
                Views.gradeSheet(service.getById(in.readRequired("Enrollment ID: ")), ctx);
                break;
            case 4:
                Views.gradingSystem();
                break;
            default:
                break;
        }
    }

    private void recordOne(EnrollmentService service) {
        Enrollment e = service.getById(in.readRequired("Enrollment ID: "));
        Views.gradeSheet(e, ctx);
        System.out.println();
        GradeComponent component = in.readEnum("Component: ", GradeComponent.class);
        double score = in.readDouble("Score (0-100): ", 0, 100);
        service.recordScore(e.getId(), component, score);
        System.out.println();
        Views.gradeSheet(e, ctx);
        success("Score saved.");
    }

    private void encodeClass(EnrollmentService service) {
        Course course = ctx.getCourseService().getById(askCourseCode());
        List<Enrollment> list = service.getGradableByCourse(course.getCode());
        if (list.isEmpty()) {
            System.out.println("No gradable students in " + course.getCode() + ".");
            return;
        }
        GradeComponent component = in.readEnum("Component to encode: ", GradeComponent.class);
        System.out.println("Enter a score from 0-100 for each student. Press Enter to skip.");

        int saved = 0;
        for (Enrollment e : list) {
            String name = ctx.getStudentService().getById(e.getStudentId()).getFullName();
            Double current = e.getGradeSheet().getScore(component);
            while (true) {
                String value = in.readLine(String.format("  %-28s [%s]: ", name,
                        current == null ? "-" : String.format("%.1f", current)));
                if (value.isEmpty()) {
                    break;
                }
                try {
                    double score = Double.parseDouble(value);
                    if (score >= 0 && score <= 100) {
                        service.recordScore(e.getId(), component, score);
                        saved++;
                        break;
                    }
                } catch (NumberFormatException ignored) {
                    // fall through to the message
                }
                System.out.println("    Enter a number from 0 to 100, or press Enter to skip.");
            }
        }
        success(saved + " score(s) saved for " + component.getLabel() + " in " + course.getCode() + ".");
    }
}
