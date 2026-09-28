import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentRecords {

    static final String FILE_NAME = "students.txt";
    static ArrayList<Student> students = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    static class Student {
        String id;
        String name;
        String course;
        double grade;

        Student(String id, String name, String course, double grade) {
            this.id = id;
            this.name = name;
            this.course = course;
            this.grade = grade;
        }

        String toFileLine() {
            return id + "|" + name + "|" + course + "|" + grade;
        }

        void display() {
            System.out.printf("%-12s %-25s %-15s %6.2f%n", id, name, course, grade);
        }
    }

    public static void main(String[] args) {
        loadFromFile();
        int choice;

        do {
            showMenu();
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewStudents();
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
                    saveToFile();
                    break;
                case 0:
                    saveToFile();
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);

        scanner.close();
    }

    static void showMenu() {
        System.out.println();
        System.out.println("===== STUDENT RECORDS =====");
        System.out.println("1. Add student");
        System.out.println("2. View all students");
        System.out.println("3. Search student");
        System.out.println("4. Update student");
        System.out.println("5. Delete student");
        System.out.println("6. Save to file");
        System.out.println("0. Save and exit");
    }

    static void addStudent() {
        String id = readText("Student ID: ");
        if (findById(id) != null) {
            System.out.println("A student with that ID already exists.");
            return;
        }
        String name = readText("Name: ");
        String course = readText("Course: ");
        double grade = readDouble("Grade: ");

        students.add(new Student(id, name, course, grade));
        saveToFile();
        System.out.println("Student added.");
    }

    static void viewStudents() {
        if (students.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-12s %-25s %-15s %6s%n", "ID", "NAME", "COURSE", "GRADE");
        System.out.println("-".repeat(62));
        for (Student s : students) {
            s.display();
        }
    }

    static void searchStudent() {
        String id = readText("Enter student ID: ");
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.printf("%-12s %-25s %-15s %6s%n", "ID", "NAME", "COURSE", "GRADE");
        System.out.println("-".repeat(62));
        s.display();
    }

    static void updateStudent() {
        String id = readText("Enter student ID to update: ");
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        s.name = readText("New name: ");
        s.course = readText("New course: ");
        s.grade = readDouble("New grade: ");

        saveToFile();
        System.out.println("Student updated.");
    }

    static void deleteStudent() {
        String id = readText("Enter student ID to delete: ");
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        students.remove(s);
        saveToFile();
        System.out.println("Student deleted.");
    }

    static Student findById(String id) {
        for (Student s : students) {
            if (s.id.equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                writer.write(s.toFileLine());
                writer.newLine();
            }
            System.out.println("Records saved to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    students.add(new Student(parts[0], parts[1], parts[2], Double.parseDouble(parts[3])));
                }
            }
            System.out.println(students.size() + " record(s) loaded.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    static String readText(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim().replace("|", " ");
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty.");
            }
        } while (input.isEmpty());
        return input;
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}
