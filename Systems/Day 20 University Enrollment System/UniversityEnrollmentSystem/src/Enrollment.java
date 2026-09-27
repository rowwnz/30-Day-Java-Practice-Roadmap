public class Enrollment {

    private final Student student;
    private final Course course;
    private Grade grade;

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        student.addEnrollment(this);
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public Grade getGrade() {
        return grade;
    }

    public void submitGrade(double gradePoint, String remarks) {
        this.grade = new Grade(gradePoint, remarks);
    }

    @Override
    public String toString() {
        String gradeText = (grade == null) ? "No grade yet" : grade.toString();
        return String.format("%s enrolled in %s -> %s",
                student.getName(), course.getCode(), gradeText);
    }
}
