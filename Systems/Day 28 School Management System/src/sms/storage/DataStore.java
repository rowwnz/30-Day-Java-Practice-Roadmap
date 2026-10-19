package sms.storage;

import sms.model.AttendanceRecord;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.Student;
import sms.model.Teacher;
import sms.repository.InMemoryRepository;
import sms.repository.Repository;
import sms.util.IdGenerator;

import java.io.Serializable;

/** Holds every repository plus the ID counters, so the whole system saves and loads as one object. */
public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Repository<Student> students = new InMemoryRepository<>();
    private final Repository<Teacher> teachers = new InMemoryRepository<>();
    private final Repository<Course> courses = new InMemoryRepository<>();
    private final Repository<Enrollment> enrollments = new InMemoryRepository<>();
    private final Repository<AttendanceRecord> attendance = new InMemoryRepository<>();
    private final IdGenerator idGenerator = new IdGenerator();

    public Repository<Student> getStudents() {
        return students;
    }

    public Repository<Teacher> getTeachers() {
        return teachers;
    }

    public Repository<Course> getCourses() {
        return courses;
    }

    public Repository<Enrollment> getEnrollments() {
        return enrollments;
    }

    public Repository<AttendanceRecord> getAttendance() {
        return attendance;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }
}
