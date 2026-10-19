package sms.service;

import sms.exception.ValidationException;
import sms.model.Course;
import sms.model.Teacher;
import sms.repository.Repository;
import sms.util.IdGenerator;
import sms.util.Validator;

import java.util.List;

public class TeacherService extends PersonService<Teacher> {
    private final Repository<Course> courses;

    public TeacherService(Repository<Teacher> teachers, Repository<Course> courses, IdGenerator ids) {
        super(teachers, ids);
        this.courses = courses;
    }

    @Override
    protected String entityName() {
        return "Teacher";
    }

    public Teacher hire(String firstName, String lastName, String email, String contact,
                        String department, String specialization) {
        String cleanFirst = Validator.requireNonBlank(firstName, "First name");
        String cleanLast = Validator.requireNonBlank(lastName, "Last name");
        String cleanEmail = Validator.requireEmail(email);
        String cleanContact = Validator.requireContact(contact);
        String cleanDept = Validator.requireNonBlank(department, "Department");
        String cleanSpec = Validator.requireNonBlank(specialization, "Specialization");
        ensureEmailUnique(cleanEmail, null);

        Teacher teacher = new Teacher(ids.next("T", 4), cleanFirst, cleanLast, cleanEmail,
                cleanContact, cleanDept, cleanSpec);
        repository.save(teacher);
        return teacher;
    }

    /** Null arguments keep the current value. */
    public Teacher update(String id, String firstName, String lastName, String email, String contact,
                          String department, String specialization) {
        Teacher teacher = getById(id);
        applyCommonUpdates(teacher, firstName, lastName, email, contact);
        if (department != null) {
            teacher.setDepartment(Validator.requireNonBlank(department, "Department"));
        }
        if (specialization != null) {
            teacher.setSpecialization(Validator.requireNonBlank(specialization, "Specialization"));
        }
        return teacher;
    }

    public List<Course> getAssignedCourses(String teacherId) {
        Teacher teacher = getById(teacherId);
        return courses.findBy(c -> teacher.getId().equals(c.getTeacherId()));
    }

    public void delete(String id) {
        Teacher teacher = getById(id);
        if (!getAssignedCourses(teacher.getId()).isEmpty()) {
            throw new ValidationException(teacher.getFullName()
                    + " is still assigned to courses. Unassign them first.");
        }
        repository.deleteById(teacher.getId());
    }
}
