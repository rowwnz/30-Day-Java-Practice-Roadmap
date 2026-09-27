import java.util.ArrayList;
import java.util.List;

public class Department {

    private final String name;
    private final List<Instructor> instructors = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void hireInstructor(Instructor instructor) {
        instructors.add(instructor);
    }

    public Course offerCourse(String code, String title, int units, String day, String time, String room) {
        Course course = new Course(code, title, units, day, time, room);
        courses.add(course);
        return course;
    }

    public List<Instructor> getInstructors() {
        return List.copyOf(instructors);
    }

    public List<Course> getCourses() {
        return List.copyOf(courses);
    }

    @Override
    public String toString() {
        return String.format("Department: %s | %d instructor(s), %d course(s)",
                name, instructors.size(), courses.size());
    }
}
