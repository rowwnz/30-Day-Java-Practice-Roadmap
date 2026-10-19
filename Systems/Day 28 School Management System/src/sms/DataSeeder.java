package sms;

import sms.model.AttendanceStatus;
import sms.model.Course;
import sms.model.Enrollment;
import sms.model.GradeComponent;
import sms.model.Student;
import sms.model.StudentStatus;
import sms.model.Teacher;
import sms.service.AttendanceService;
import sms.service.CourseService;
import sms.service.EnrollmentService;
import sms.service.StudentService;
import sms.service.TeacherService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Fills an empty system with sample records on the first run, using the real services. */
public final class DataSeeder {

    private DataSeeder() {
    }

    public static void seed(AppContext ctx) {
        Random random = new Random(42);
        TeacherService teachers = ctx.getTeacherService();
        CourseService courses = ctx.getCourseService();
        StudentService students = ctx.getStudentService();
        EnrollmentService enrollments = ctx.getEnrollmentService();
        AttendanceService attendance = ctx.getAttendanceService();

        Teacher santos = teachers.hire("Maria", "Santos", "maria.santos@school.edu", "09171234567",
                "Computer Science", "Software Engineering");
        Teacher reyes = teachers.hire("Jose", "Reyes", "jose.reyes@school.edu", "09181234567",
                "Computer Science", "Algorithms");
        Teacher cruz = teachers.hire("Ana", "Cruz", "ana.cruz@school.edu", "09191234567",
                "Mathematics", "Discrete Mathematics");

        Course comp = courses.create("COMP101", "Introduction to Computing", 3, 40);
        Course prog = courses.create("PROG101", "Computer Programming 1 (Java)", 3, 40);
        Course math = courses.create("MATH101", "Discrete Mathematics", 3, 35);
        Course web = courses.create("WEB101", "Web Development", 3, 30);
        courses.create("DSA201", "Data Structures and Algorithms", 3, 30);
        courses.assignTeacher(comp.getCode(), santos.getId());
        courses.assignTeacher(prog.getCode(), santos.getId());
        courses.assignTeacher(math.getCode(), cruz.getId());
        courses.assignTeacher(web.getCode(), reyes.getId());

        String[][] people = {
                {"Juan", "Dela Cruz"}, {"Bea", "Villanueva"}, {"Carlo", "Mendoza"}, {"Dana", "Garcia"},
                {"Enzo", "Ramos"}, {"Faith", "Aquino"}, {"Gabriel", "Torres"}, {"Hannah", "Bautista"}
        };
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < people.length; i++) {
            String email = (people[i][0] + "." + people[i][1]).toLowerCase().replace(" ", "") + "@students.school.edu";
            list.add(students.register(people[i][0], people[i][1], email, "0917555" + String.format("%04d", i),
                    "BSCS", 1 + (i % 2)));
        }

        // Previous term: everyone finished COMP101, which gives each student a GWA.
        String previousTerm = "AY 2025-2026 T3";
        for (Student s : list.subList(0, 7)) {
            Enrollment e = enrollments.enroll(s.getId(), comp.getCode(), previousTerm);
            for (GradeComponent component : GradeComponent.values()) {
                enrollments.recordScore(e.getId(), component, randomScore(random));
            }
        }

        // Current term: partial grades and a few days of attendance.
        String currentTerm = AppContext.DEFAULT_TERM;
        List<LocalDate> classDays = recentWeekdays(6);
        for (Student s : list.subList(0, 7)) {
            for (Course c : new Course[]{prog, math, web}) {
                if (c == web && random.nextBoolean()) {
                    continue;
                }
                Enrollment e = enrollments.enroll(s.getId(), c.getCode(), currentTerm);
                enrollments.recordScore(e.getId(), GradeComponent.QUIZZES, randomScore(random));
                enrollments.recordScore(e.getId(), GradeComponent.INTERIM_EXAM, randomScore(random));
                for (LocalDate day : classDays) {
                    attendance.mark(s.getId(), c.getCode(), day, randomAttendance(random));
                }
            }
        }

        // The last sample student is on leave.
        students.changeStatus(list.get(7).getId(), StudentStatus.INACTIVE);
    }

    private static double randomScore(Random random) {
        return 68 + random.nextInt(31) + random.nextInt(10) / 10.0;
    }

    private static AttendanceStatus randomAttendance(Random random) {
        int roll = random.nextInt(100);
        if (roll < 80) {
            return AttendanceStatus.PRESENT;
        }
        if (roll < 90) {
            return AttendanceStatus.LATE;
        }
        return roll < 96 ? AttendanceStatus.ABSENT : AttendanceStatus.EXCUSED;
    }

    private static List<LocalDate> recentWeekdays(int count) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate day = LocalDate.now().minusDays(1);
        while (days.size() < count) {
            if (day.getDayOfWeek() != DayOfWeek.SATURDAY && day.getDayOfWeek() != DayOfWeek.SUNDAY) {
                days.add(0, day);
            }
            day = day.minusDays(1);
        }
        return days;
    }
}
