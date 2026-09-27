import java.util.ArrayList;
import java.util.List;

public class University {

    private final String name;
    private final List<Department> departments = new ArrayList<>();
    private final List<Student> studentBody = new ArrayList<>();

    public University(String name) {
        this.name = name;
    }

    public Department addDepartment(String departmentName) {
        Department department = new Department(departmentName);
        departments.add(department);
        return department;
    }

    public void admit(Student student) {
        studentBody.add(student);
    }

    public List<Department> getDepartments() {
        return List.copyOf(departments);
    }

    public List<Student> getStudentBody() {
        return List.copyOf(studentBody);
    }

    public void printReport() {
        System.out.println("=== " + name + " - Enrollment Report ===\n");

        for (Department d : departments) {
            System.out.println(d);
            for (Instructor i : d.getInstructors()) {
                System.out.println("  " + i.describe());
            }
            for (Course c : d.getCourses()) {
                System.out.println("  " + c);
            }
            System.out.println();
        }

        System.out.println("--- Students ---");
        for (Student s : studentBody) {
            System.out.println(s.describe());
            for (Enrollment e : s.getEnrollments()) {
                System.out.println("    " + e);
            }
            System.out.printf("    GPA: %.2f%n", s.getGpa());
        }
    }
}
