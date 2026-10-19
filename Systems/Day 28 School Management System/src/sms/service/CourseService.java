package sms.service;

import sms.exception.DuplicateEntityException;
import sms.exception.EntityNotFoundException;
import sms.exception.ValidationException;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.Teacher;
import sms.repository.Repository;
import sms.util.Validator;

import java.util.Comparator;
import java.util.List;

public class CourseService {
    private final Repository<Course> courses;
    private final Repository<Enrollment> enrollments;
    private final TeacherService teacherService;

    public CourseService(Repository<Course> courses, Repository<Enrollment> enrollments,
                         TeacherService teacherService) {
        this.courses = courses;
        this.enrollments = enrollments;
        this.teacherService = teacherService;
    }

    public Course create(String code, String title, int units, int capacity) {
        String cleanCode = Validator.requireCourseCode(code);
        if (courses.existsById(cleanCode)) {
            throw new DuplicateEntityException("Course " + cleanCode + " already exists.");
        }
        Course course = new Course(cleanCode, Validator.requireNonBlank(title, "Title"),
                Validator.requireRange(units, 1, 6, "Units"),
                Validator.requireRange(capacity, 1, 100, "Capacity"));
        courses.save(course);
        return course;
    }

    public Course getById(String code) {
        return courses.findById(code).orElseThrow(() -> new EntityNotFoundException("Course", code));
    }

    public List<Course> getAll() {
        List<Course> all = courses.findAll();
        all.sort(Comparator.comparing(Course::getCode));
        return all;
    }

    public int count() {
        return courses.count();
    }

    /** Null arguments keep the current value. */
    public Course update(String code, String title, Integer units, Integer capacity) {
        Course course = getById(code);
        if (title != null) {
            course.setTitle(Validator.requireNonBlank(title, "Title"));
        }
        if (units != null) {
            course.setUnits(Validator.requireRange(units, 1, 6, "Units"));
        }
        if (capacity != null) {
            int active = countActiveEnrollments(course.getCode());
            if (capacity < active) {
                throw new ValidationException("Capacity cannot be lower than the " + active
                        + " students currently enrolled.");
            }
            course.setCapacity(Validator.requireRange(capacity, 1, 100, "Capacity"));
        }
        return course;
    }

    public void assignTeacher(String code, String teacherId) {
        Course course = getById(code);
        Teacher teacher = teacherService.getById(teacherId);
        course.setTeacherId(teacher.getId());
    }

    public void unassignTeacher(String code) {
        Course course = getById(code);
        if (!course.hasTeacher()) {
            throw new ValidationException(course.getCode() + " has no assigned teacher.");
        }
        course.setTeacherId(null);
    }

    public String getTeacherName(Course course) {
        if (!course.hasTeacher()) {
            return "Unassigned";
        }
        return teacherService.getById(course.getTeacherId()).getFullName();
    }

    public int countActiveEnrollments(String code) {
        String cleanCode = code.trim().toUpperCase();
        return enrollments.findBy(e -> e.getCourseCode().equals(cleanCode) && e.isActive()).size();
    }

    public void delete(String code) {
        Course course = getById(code);
        boolean hasRecords = !enrollments.findBy(e -> e.getCourseCode().equals(course.getCode())).isEmpty();
        if (hasRecords) {
            throw new ValidationException("Course " + course.getCode()
                    + " has enrollment records and cannot be deleted.");
        }
        courses.deleteById(course.getCode());
    }
}
