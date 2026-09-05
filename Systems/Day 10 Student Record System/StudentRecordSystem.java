import java.util.Scanner;

public class StudentRecordSystem {

    static class Student {
        int id;
        String name;
        String course;
        int yearLevel;

        Student(int id, String name, String course, int yearLevel) {
            this.id = id;
            this.name = name;
            this.course = course;
            this.yearLevel = yearLevel;
        }

        @Override
        public String toString() {
            return String.format("%-6d %-20s %-15s %-10d", id, name, course, yearLevel);
        }
    }


    static final int MAX_STUDENTS = 100;
    static Student[] students = new Student[MAX_STUDENTS];
    static int count = 0; // number of records currently stored
    static int nextId = 1; // auto-incrementing ID

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            printMenu();
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> createStudent();
                case 2 -> readAllStudents();
                case 3 -> readStudentById();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 6 -> System.out.println("Exiting program. Goodbye!");
                default -> System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        } while (choice != 6);

        sc.close();
    }

    static void printMenu() {
        System.out.println("=========================================");
        System.out.println("          STUDENT RECORD SYSTEM         ");
        System.out.println("=========================================");
        System.out.println("1. Add Student        (Create)");
        System.out.println("2. View All Students   (Read)");
        System.out.println("3. Search Student      (Read)");
        System.out.println("4. Update Student      (Update)");
        System.out.println("5. Delete Student      (Delete)");
        System.out.println("6. Exit");
        System.out.println("=========================================");
    }


    static void createStudent() {
        if (count >= MAX_STUDENTS) {
            System.out.println("Storage is full. Cannot add more students.");
            return;
        }

        System.out.println("--- Add New Student ---");
        String name = readString("Enter name: ");
        String course = readString("Enter course: ");
        int yearLevel = readInt("Enter year level: ");

        students[count] = new Student(nextId, name, course, yearLevel);
        count++;
        System.out.println("Student added successfully with ID: " + nextId);
        nextId++;
    }

    //READ (all)
    static void readAllStudents() {
        System.out.println("--- All Students ---");
        if (count == 0) {
            System.out.println("No student records found.");
            return;
        }

        printTableHeader();
        for (int i = 0; i < count; i++) {
            System.out.println(students[i]);
        }
    }

    //READ (search by ID) 
    static void readStudentById() {
        if (count == 0) {
            System.out.println("No student records found.");
            return;
        }

        int id = readInt("Enter Student ID to search: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Student with ID " + id + " not found.");
        } else {
            System.out.println("--- Student Found ---");
            printTableHeader();
            System.out.println(students[index]);
        }
    }

    // UPDATE
    static void updateStudent() {
        if (count == 0) {
            System.out.println("No student records found.");
            return;
        }

        int id = readInt("Enter Student ID to update: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Student with ID " + id + " not found.");
            return;
        }

        Student s = students[index];
        System.out.println("Current details: " + s);
        System.out.println("Leave a field blank to keep its current value.");

        String name = readString("Enter new name [" + s.name + "]: ");
        if (!name.isBlank()) s.name = name;

        String course = readString("Enter new course [" + s.course + "]: ");
        if (!course.isBlank()) s.course = course;

        String yearInput = readString("Enter new year level [" + s.yearLevel + "]: ");
        if (!yearInput.isBlank()) {
            try {
                s.yearLevel = Integer.parseInt(yearInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Year level unchanged.");
            }
        }

        System.out.println("Student updated successfully.");
    }

    //DELETE 
    static void deleteStudent() {
        if (count == 0) {
            System.out.println("No student records found.");
            return;
        }

        int id = readInt("Enter Student ID to delete: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Student with ID " + id + " not found.");
            return;
        }

        // Shift all subsequent elements left by one to fill the gap
        for (int i = index; i < count - 1; i++) {
            students[i] = students[i + 1];
        }
        students[count - 1] = null; // clear the last (now duplicate) slot
        count--;

        System.out.println("Student with ID " + id + " deleted successfully.");
    }

    //Helpers
    static int findIndexById(int id) {
        for (int i = 0; i < count; i++) {
            if (students[i].id == id) {
                return i;
            }
        }
        return -1;
    }

    static void printTableHeader() {
        System.out.printf("%-6s %-20s %-15s %-10s%n", "ID", "Name", "Course", "Year Level");
        System.out.println("-------------------------------------------------------");
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}
