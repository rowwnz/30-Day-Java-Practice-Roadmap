package sms.ui;

import sms.AppContext;

public class MainMenu extends Menu {
    private final Runnable saveAction;
    private final Menu[] subMenus;

    public MainMenu(AppContext ctx, Runnable saveAction) {
        super("SCHOOL MANAGEMENT SYSTEM", ctx);
        this.saveAction = saveAction;
        // Polymorphism: every entry is handled through the Menu type.
        this.subMenus = new Menu[]{
                new StudentMenu(ctx),
                new TeacherMenu(ctx),
                new CourseMenu(ctx),
                new EnrollmentMenu(ctx),
                new GradeMenu(ctx),
                new AttendanceMenu(ctx),
                new ReportMenu(ctx)
        };
    }

    @Override
    protected String[] options() {
        return new String[]{
                "Students",
                "Teachers",
                "Courses",
                "Enrollment",
                "Grades",
                "Attendance",
                "Reports",
                "Save data now"
        };
    }

    @Override
    protected String exitLabel() {
        return "Save & Exit";
    }

    @Override
    public void show() {
        // Sub-menus run their own loops, so the main menu skips the "Press Enter" pause for them.
        while (true) {
            System.out.println();
            printHeader("SCHOOL MANAGEMENT SYSTEM");
            String[] options = options();
            for (int i = 0; i < options.length; i++) {
                System.out.printf("  %d. %s%n", i + 1, options[i]);
            }
            System.out.printf("  0. %s%n", exitLabel());
            int choice = in.readInt("Select: ", 0, options.length);
            if (choice == 0) {
                return;
            }
            handle(choice);
        }
    }

    @Override
    protected void handle(int choice) {
        if (choice <= subMenus.length) {
            subMenus[choice - 1].show();
        } else {
            saveAction.run();
        }
    }
}
