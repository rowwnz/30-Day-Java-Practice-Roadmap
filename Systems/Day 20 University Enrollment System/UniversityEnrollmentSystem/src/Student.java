import java.util.ArrayList;
import java.util.List;

public class Student extends Person {

    private final String yearLevel;
    private final List<Enrollment> enrollments = new ArrayList<>();

    public Student(String id, String name, String email, String yearLevel) {
        super(id, name, email);
        this.yearLevel = yearLevel;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public List<Enrollment> getEnrollments() {
        return List.copyOf(enrollments);
    }

    public double getGpa() {
        return enrollments.stream()
                .filter(e -> e.getGrade() != null)
                .mapToDouble(e -> e.getGrade().getGradePoint())
                .average()
                .orElse(0.0);
    }

    @Override
    public String describe() {
        return String.format("Student[%s] %s (Year %s) - %d unit(s) enrolled",
                getId(), getName(), yearLevel, enrollments.size());
    }
}
