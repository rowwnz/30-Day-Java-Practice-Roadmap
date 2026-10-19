package sms.ui;

import sms.AppContext;
import sms.exception.SmsException;
import sms.util.InputHelper;

/**
 * Base class for every console menu (Template Method pattern).
 * show() runs the loop; subclasses only supply their options and handle the choice.
 */
public abstract class Menu {
    protected final AppContext ctx;
    protected final InputHelper in;
    private final String title;

    protected Menu(String title, AppContext ctx) {
        this.title = title;
        this.ctx = ctx;
        this.in = ctx.getInput();
    }

    protected abstract String[] options();

    protected abstract void handle(int choice);

    protected String exitLabel() {
        return "Back";
    }

    public void show() {
        while (true) {
            System.out.println();
            printHeader(title);
            String[] options = options();
            for (int i = 0; i < options.length; i++) {
                System.out.printf("  %d. %s%n", i + 1, options[i]);
            }
            System.out.printf("  0. %s%n", exitLabel());

            int choice = in.readInt("Select: ", 0, options.length);
            if (choice == 0) {
                return;
            }
            System.out.println();
            try {
                handle(choice);
            } catch (SmsException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
            in.pause();
        }
    }

    protected static void printHeader(String text) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < text.length() + 8; i++) {
            line.append('=');
        }
        System.out.println(line);
        System.out.println("    " + text);
        System.out.println(line);
    }

    protected void success(String message) {
        System.out.println("[OK] " + message);
    }

    /** Typing ? lists the records, then asks again. */
    protected String askStudentId() {
        while (true) {
            String value = in.readRequired("Student ID (? to list): ");
            if (!value.equals("?")) {
                return value;
            }
            Views.students(ctx.getStudentService().getAll());
        }
    }

    protected String askTeacherId() {
        while (true) {
            String value = in.readRequired("Teacher ID (? to list): ");
            if (!value.equals("?")) {
                return value;
            }
            Views.teachers(ctx.getTeacherService().getAll(), ctx);
        }
    }

    protected String askCourseCode() {
        while (true) {
            String value = in.readRequired("Course code (? to list): ");
            if (!value.equals("?")) {
                return value;
            }
            Views.courses(ctx.getCourseService().getAll(), ctx);
        }
    }
}
