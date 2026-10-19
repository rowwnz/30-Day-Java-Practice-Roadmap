package sms.service;

import sms.exception.EnrollmentException;
import sms.exception.EntityNotFoundException;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.EnrollmentStatus;
import sms.model.GradeComponent;
import sms.model.Student;
import sms.model.StudentStatus;
import sms.repository.Repository;
import sms.util.IdGenerator;
import sms.util.Validator;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EnrollmentService {
    private final Repository<Enrollment> enrollments;
    private final StudentService studentService;
    private final CourseService courseService;
    private final IdGenerator ids;

    public EnrollmentService(Repository<Enrollment> enrollments, StudentService studentService,
                             CourseService courseService, IdGenerator ids) {
        this.enrollments = enrollments;
        this.studentService = studentService;
        this.courseService = courseService;
        this.ids = ids;
    }

    public Enrollment enroll(String studentId, String courseCode, String term) {
        Student student = studentService.getById(studentId);
        Course course = courseService.getById(courseCode);
        String cleanTerm = Validator.requireNonBlank(term, "Term");

        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new EnrollmentException("Only active students can enroll. "
                    + student.getFullName() + " is " + student.getStatus() + ".");
        }
        if (findActive(student.getId(), course.getCode()).isPresent()) {
            throw new EnrollmentException(student.getFullName() + " is already enrolled in " + course.getCode() + ".");
        }
        boolean alreadyPassed = !enrollments.findBy(e -> e.getStudentId().equals(student.getId())
                && e.getCourseCode().equals(course.getCode()) && e.isPassed()).isEmpty();
        if (alreadyPassed) {
            throw new EnrollmentException(student.getFullName() + " has already passed " + course.getCode() + ".");
        }
        int active = courseService.countActiveEnrollments(course.getCode());
        if (active >= course.getCapacity()) {
            throw new EnrollmentException("Course " + course.getCode() + " is full ("
                    + active + "/" + course.getCapacity() + ").");
        }

        Enrollment enrollment = new Enrollment(ids.next("E", 5), student.getId(), course.getCode(), cleanTerm);
        enrollments.save(enrollment);
        return enrollment;
    }

    public void drop(String enrollmentId) {
        Enrollment enrollment = getById(enrollmentId);
        if (!enrollment.isActive()) {
            throw new EnrollmentException("Only active enrollments can be dropped (current status: "
                    + enrollment.getStatus() + ").");
        }
        enrollment.setStatus(EnrollmentStatus.DROPPED);
    }

    /**
     * Records one component score. Once every component has a score the enrollment
     * is marked Completed. Completed grades can still be corrected; dropped ones cannot.
     */
    public Enrollment recordScore(String enrollmentId, GradeComponent component, double score) {
        Enrollment enrollment = getById(enrollmentId);
        if (enrollment.getStatus() == EnrollmentStatus.DROPPED) {
            throw new EnrollmentException("Cannot record grades for a dropped enrollment.");
        }
        enrollment.getGradeSheet().setScore(component, score);
        if (enrollment.getGradeSheet().isComplete()) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }
        return enrollment;
    }

    public Enrollment getById(String id) {
        return enrollments.findById(id).orElseThrow(() -> new EntityNotFoundException("Enrollment", id));
    }

    public Optional<Enrollment> findActive(String studentId, String courseCode) {
        List<Enrollment> found = enrollments.findBy(e -> e.getStudentId().equalsIgnoreCase(studentId.trim())
                && e.getCourseCode().equalsIgnoreCase(courseCode.trim()) && e.isActive());
        return found.isEmpty() ? Optional.<Enrollment>empty() : Optional.of(found.get(0));
    }

    public List<Enrollment> getByStudent(String studentId) {
        Student student = studentService.getById(studentId);
        List<Enrollment> list = enrollments.findBy(e -> e.getStudentId().equals(student.getId()));
        list.sort(Comparator.comparing(Enrollment::getTerm).thenComparing(Enrollment::getCourseCode));
        return list;
    }

    public List<Enrollment> getByCourse(String courseCode) {
        Course course = courseService.getById(courseCode);
        List<Enrollment> list = enrollments.findBy(e -> e.getCourseCode().equals(course.getCode()));
        list.sort(Comparator.comparing((Enrollment e) -> studentService.getById(e.getStudentId()).getLastName(),
                String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    /** Enrollments that can still receive grades (enrolled or completed, not dropped). */
    public List<Enrollment> getGradableByCourse(String courseCode) {
        List<Enrollment> list = getByCourse(courseCode);
        list.removeIf(e -> e.getStatus() == EnrollmentStatus.DROPPED);
        return list;
    }

    public List<Enrollment> getActiveByCourse(String courseCode) {
        List<Enrollment> list = getByCourse(courseCode);
        list.removeIf(e -> !e.isActive());
        return list;
    }

    public int countByStatus(EnrollmentStatus status) {
        return enrollments.findBy(e -> e.getStatus() == status).size();
    }
}
