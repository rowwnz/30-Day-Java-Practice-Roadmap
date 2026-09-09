import java.util.ArrayList;
import java.util.Scanner;

public class StudentManagementSystem {

    // Holds all Student records in memory for the duration of the program.
    private ArrayList<Student> studentList;
    private Scanner scanner;

    public StudentManagementSystem() {
        studentList = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        StudentManagementSystem system = new StudentManagementSystem();
        system.run();
    }

    // Main program loop: shows the menu and dispatches to the matching method.
    public void run() {
        int choice;
        do {
            displayMenu();
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    deleteStudent();
                    break;
                case 6:
                    System.out.println("Exiting Student Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number from 1 to 6.");
            }
            System.out.println();
        } while (choice != 6);

        scanner.close();
    }

    private void displayMenu() {
        System.out.println("=========================================");
        System.out.println("        STUDENT MANAGEMENT SYSTEM       ");
        System.out.println("=========================================");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
        System.out.println("-----------------------------------------");
    }

    //Core methods

    private void addStudent() {
        System.out.println("\n--- Add Student ---");
        String id = readString("Student ID: ");

        if (findStudentById(id) != null) {
            System.out.println("A student with ID " + id + " already exists.");
            return;
        }

        String name = readString("Name: ");
        String course = readString("Course: ");
        int yearLevel = readInt("Year Level: ");
        double gpa = readDouble("GPA: ");

        Student student = new Student(id, name, course, yearLevel, gpa);
        studentList.add(student);
        System.out.println("Student added successfully.");
    }

    private void viewAllStudents() {
        System.out.println("\n--- All Students ---");
        if (studentList.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        System.out.printf("%-10s %-20s %-15s %-6s %-5s%n",
                "ID", "Name", "Course", "Year", "GPA");
        System.out.println("--------------------------------------------------------");
        for (Student s : studentList) {
            System.out.println(s);
        }
    }

    private void searchStudent() {
        System.out.println("\n--- Search Student ---");
        String id = readString("Enter Student ID to search: ");
        Student student = findStudentById(id);

        if (student == null) {
            System.out.println("No student found with ID " + id + ".");
        } else {
            System.out.println("Student found:");
            System.out.println(student);
        }
    }

    private void updateStudent() {
        System.out.println("\n--- Update Student ---");
        String id = readString("Enter Student ID to update: ");
        Student student = findStudentById(id);

        if (student == null) {
            System.out.println("No student found with ID " + id + ".");
            return;
        }

        System.out.println("Current record: " + student);
        System.out.println("Leave a field blank to keep its current value.");

        String name = readString("New Name: ");
        if (!name.isEmpty()) {
            student.setName(name);
        }

        String course = readString("New Course: ");
        if (!course.isEmpty()) {
            student.setCourse(course);
        }

        String yearInput = readString("New Year Level: ");
        if (!yearInput.isEmpty()) {
            student.setYearLevel(Integer.parseInt(yearInput));
        }

        String gpaInput = readString("New GPA: ");
        if (!gpaInput.isEmpty()) {
            student.setGpa(Double.parseDouble(gpaInput));
        }

        System.out.println("Student updated successfully.");
    }

    private void deleteStudent() {
        System.out.println("\n--- Delete Student ---");
        String id = readString("Enter Student ID to delete: ");
        Student student = findStudentById(id);

        if (student == null) {
            System.out.println("No student found with ID " + id + ".");
            return;
        }

        studentList.remove(student);
        System.out.println("Student with ID " + id + " has been deleted.");
    }

    //Helper methods 

    // Searches the list for a student with the given ID; returns null if not found.
    private Student findStudentById(String id) {
        for (Student s : studentList) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}


class Student {
    private String id;
    private String name;
    private String course;
    private int yearLevel;
    private double gpa;

    public Student(String id, String name, String course, int yearLevel, double gpa) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.yearLevel = yearLevel;
        this.gpa = gpa;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public double getGpa() {
        return gpa;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    @Override
    public String toString() {
        return String.format("%-10s %-20s %-15s %-6d %-5.2f", id, name, course, yearLevel, gpa);
    }
}
