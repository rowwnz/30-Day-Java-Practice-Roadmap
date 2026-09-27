public class Main {

    public static void main(String[] args) {
        University nu = new University("Notional University");

        Department ccs = nu.addDepartment("College of Computer Studies");

        Instructor profSantos = new Instructor("I-001", "Ana Santos", "asantos@nu.edu",
                "Associate Professor", 650.0);
        Instructor profReyes = new Instructor("I-002", "Mark Reyes", "mreyes@nu.edu",
                "Assistant Professor", 550.0);
        ccs.hireInstructor(profSantos);
        ccs.hireInstructor(profReyes);

        Course java101 = ccs.offerCourse("CCPRGG1L", "Java Programming", 3,
                "MWF", "9:00-10:30", "Rm 301");
        Course intro101 = ccs.offerCourse("CCINCOML", "Introduction to Computing", 3,
                "TTh", "13:00-14:30", "Rm 204");

        java101.assignInstructor(profSantos);
        intro101.assignInstructor(profReyes);

        Student juan = new Student("S-1001", "Juan Dela Cruz", "juan@nu.edu", "1st Year");
        Student maria = new Student("S-1002", "Maria Clara", "maria@nu.edu", "1st Year");
        nu.admit(juan);
        nu.admit(maria);

        Enrollment e1 = new Enrollment(juan, java101);
        Enrollment e2 = new Enrollment(juan, intro101);
        Enrollment e3 = new Enrollment(maria, java101);

        e1.submitGrade(1.50, "Passed");
        e2.submitGrade(1.75, "Passed");
        e3.submitGrade(2.00, "Passed");

        nu.printReport();
    }
}
