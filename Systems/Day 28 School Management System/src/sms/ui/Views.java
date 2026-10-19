package sms.ui;

import sms.AppContext;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.GradeComponent;
import sms.model.GradeScale;
import sms.model.Student;
import sms.model.Teacher;
import sms.util.TablePrinter;

import java.util.List;
import java.util.OptionalDouble;

/** Shared table renderers used by several menus. */
final class Views {

    private Views() {
    }

    static void students(List<Student> students) {
        TablePrinter table = new TablePrinter("ID", "Name", "Program", "Year", "Email", "Status");
        for (Student s : students) {
            table.addRow(s.getId(), s.getFullName(), s.getProgram(), s.getYearLevel(), s.getEmail(), s.getStatus());
        }
        printOrEmpty(table, "No students found.");
    }

    static void teachers(List<Teacher> teachers, AppContext ctx) {
        TablePrinter table = new TablePrinter("ID", "Name", "Department", "Specialization", "Courses");
        for (Teacher t : teachers) {
            table.addRow(t.getId(), t.getFullName(), t.getDepartment(), t.getSpecialization(),
                    ctx.getTeacherService().getAssignedCourses(t.getId()).size());
        }
        printOrEmpty(table, "No teachers found.");
    }

    static void courses(List<Course> courses, AppContext ctx) {
        TablePrinter table = new TablePrinter("Code", "Title", "Units", "Teacher", "Enrolled");
        for (Course c : courses) {
            table.addRow(c.getCode(), c.getTitle(), c.getUnits(), ctx.getCourseService().getTeacherName(c),
                    ctx.getCourseService().countActiveEnrollments(c.getCode()) + "/" + c.getCapacity());
        }
        printOrEmpty(table, "No courses found.");
    }

    static void enrollments(List<Enrollment> enrollments, AppContext ctx) {
        TablePrinter table = new TablePrinter("Enroll ID", "Student", "Course", "Term", "Status", "Grade %", "NU");
        for (Enrollment e : enrollments) {
            Student s = ctx.getStudentService().getById(e.getStudentId());
            OptionalDouble grade = e.getFinalGrade();
            table.addRow(e.getId(), s.getFullName(), e.getCourseCode(), e.getTerm(), e.getStatus(),
                    grade.isPresent() ? String.format("%.2f", grade.getAsDouble()) : "-",
                    e.getGradeScale().map(GradeScale::getLabel).orElse("-"));
        }
        printOrEmpty(table, "No enrollments found.");
    }

    static void gradeSheet(Enrollment e, AppContext ctx) {
        Student s = ctx.getStudentService().getById(e.getStudentId());
        Course c = ctx.getCourseService().getById(e.getCourseCode());
        System.out.println("Grade sheet " + e.getId() + ": " + s.getFullName() + " - " + c + " (" + e.getTerm() + ")");
        TablePrinter table = new TablePrinter("Component", "Weight", "Score");
        for (GradeComponent component : GradeComponent.values()) {
            Double score = e.getGradeSheet().getScore(component);
            table.addRow(component.getLabel(), Math.round(component.getWeight() * 100) + "%",
                    score == null ? "not yet recorded" : String.format("%.2f", score));
        }
        table.print();
        OptionalDouble finalGrade = e.getFinalGrade();
        if (finalGrade.isPresent()) {
            GradeScale scale = GradeScale.fromScore(finalGrade.getAsDouble());
            System.out.printf("Final grade: %.2f -> %s (%s)%n", finalGrade.getAsDouble(),
                    scale.getLabel(), scale.getDescription());
        } else {
            OptionalDouble running = e.getGradeSheet().getRunningGrade();
            System.out.println("Final grade: incomplete"
                    + (running.isPresent() ? String.format(" (running grade %.2f)", running.getAsDouble()) : ""));
        }
        System.out.println("Status: " + e.getStatus());
    }

    static void gradingSystem() {
        System.out.println("Grade components:");
        TablePrinter weights = new TablePrinter("Component", "Weight");
        for (GradeComponent c : GradeComponent.values()) {
            weights.addRow(c.getLabel(), Math.round(c.getWeight() * 100) + "%");
        }
        weights.print();
        System.out.println("\nNU Grade Point scale:");
        TablePrinter scale = new TablePrinter("Grade", "Range", "Description");
        for (GradeScale g : GradeScale.values()) {
            String range = g == GradeScale.REPEAT ? "below 60" : g.getMinScore() + "-" + g.getMaxScore();
            scale.addRow(g.getLabel(), range, g.getDescription());
        }
        scale.print();
    }

    private static void printOrEmpty(TablePrinter table, String emptyMessage) {
        if (table.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            table.print();
        }
    }
}
