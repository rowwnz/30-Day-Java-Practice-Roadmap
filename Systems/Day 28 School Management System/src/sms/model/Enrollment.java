package sms.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;
import java.util.OptionalDouble;

/** Links one student to one course for one term, and owns that pairing's grade sheet. */
public class Enrollment implements Identifiable, Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String studentId;
    private final String courseCode;
    private final String term;
    private final LocalDate dateEnrolled;
    private final GradeSheet gradeSheet = new GradeSheet();
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    public Enrollment(String id, String studentId, String courseCode, String term) {
        this.id = id;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.term = term;
        this.dateEnrolled = LocalDate.now();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTerm() {
        return term;
    }

    public LocalDate getDateEnrolled() {
        return dateEnrolled;
    }

    public GradeSheet getGradeSheet() {
        return gradeSheet;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == EnrollmentStatus.ENROLLED;
    }

    public OptionalDouble getFinalGrade() {
        return gradeSheet.getFinalGrade();
    }

    public Optional<GradeScale> getGradeScale() {
        OptionalDouble finalGrade = getFinalGrade();
        return finalGrade.isPresent()
                ? Optional.of(GradeScale.fromScore(finalGrade.getAsDouble()))
                : Optional.<GradeScale>empty();
    }

    public boolean isPassed() {
        return status == EnrollmentStatus.COMPLETED && getGradeScale().map(GradeScale::isPassing).orElse(false);
    }
}
