package sms.service;

import sms.exception.ValidationException;
import sms.model.Enrollment;
import sms.model.Student;
import sms.model.StudentStatus;
import sms.repository.Repository;
import sms.util.IdGenerator;
import sms.util.Validator;

public class StudentService extends PersonService<Student> {
    private final Repository<Enrollment> enrollments;

    public StudentService(Repository<Student> students, Repository<Enrollment> enrollments, IdGenerator ids) {
        super(students, ids);
        this.enrollments = enrollments;
    }

    @Override
    protected String entityName() {
        return "Student";
    }

    public Student register(String firstName, String lastName, String email, String contact,
                            String program, int yearLevel) {
        String cleanFirst = Validator.requireNonBlank(firstName, "First name");
        String cleanLast = Validator.requireNonBlank(lastName, "Last name");
        String cleanEmail = Validator.requireEmail(email);
        String cleanContact = Validator.requireContact(contact);
        String cleanProgram = Validator.requireNonBlank(program, "Program").toUpperCase();
        Validator.requireRange(yearLevel, 1, 5, "Year level");
        ensureEmailUnique(cleanEmail, null);

        Student student = new Student(ids.next("S", 4), cleanFirst, cleanLast, cleanEmail,
                cleanContact, cleanProgram, yearLevel);
        repository.save(student);
        return student;
    }

    /** Null arguments keep the current value. */
    public Student update(String id, String firstName, String lastName, String email, String contact,
                          String program, Integer yearLevel) {
        Student student = getById(id);
        applyCommonUpdates(student, firstName, lastName, email, contact);
        if (program != null) {
            student.setProgram(Validator.requireNonBlank(program, "Program").toUpperCase());
        }
        if (yearLevel != null) {
            student.setYearLevel(Validator.requireRange(yearLevel, 1, 5, "Year level"));
        }
        return student;
    }

    public void changeStatus(String id, StudentStatus status) {
        Student student = getById(id);
        if (status != StudentStatus.ACTIVE && hasActiveEnrollments(student.getId())) {
            throw new ValidationException(student.getFullName()
                    + " still has active enrollments. Drop or complete them first.");
        }
        student.setStatus(status);
    }

    public void delete(String id) {
        Student student = getById(id);
        boolean hasRecords = !enrollments.findBy(e -> e.getStudentId().equals(student.getId())).isEmpty();
        if (hasRecords) {
            throw new ValidationException("Student has academic records and cannot be deleted. "
                    + "Set the status to Inactive instead.");
        }
        repository.deleteById(student.getId());
    }

    public long countActive() {
        return repository.findBy(s -> s.getStatus() == StudentStatus.ACTIVE).size();
    }

    private boolean hasActiveEnrollments(String studentId) {
        return !enrollments.findBy(e -> e.getStudentId().equals(studentId) && e.isActive()).isEmpty();
    }
}
