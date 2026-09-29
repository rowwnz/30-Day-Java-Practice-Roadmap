import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AttendanceSystem {

    enum Status { PRESENT, ABSENT, LATE }

    static class Student {
        private final String id;
        private final String name;
        private final Map<LocalDate, Status> records = new TreeMap<>();

        Student(String id, String name) {
            this.id = id;
            this.name = name;
        }

        String getId() { return id; }
        String getName() { return name; }
        Map<LocalDate, Status> getRecords() { return records; }

        int count(Status s) {
            int c = 0;
            for (Status v : records.values()) {
                if (v == s) c++;
            }
            return c;
        }
    }

    private static final String DATA_FILE = "attendance.csv";
    private static final Map<String, Student> students = new LinkedHashMap<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        boolean running = true;

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addStudent(); break;
                case "2": markAttendance(); break;
                case "3": viewStudentRecord(); break;
                case "4": viewByDate(); break;
                case "5": viewSummary(); break;
                case "6": removeStudent(); break;
                case "7": saveData(); System.out.println("Data saved."); break;
                case "0":
                    saveData();
                    System.out.println("Data saved. Goodbye.");
                    running = false;
                    break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n===== ATTENDANCE SYSTEM =====");
        System.out.println("1. Add student");
        System.out.println("2. Mark attendance");
        System.out.println("3. View a student's record");
        System.out.println("4. View attendance by date");
        System.out.println("5. Summary report");
        System.out.println("6. Remove student");
        System.out.println("7. Save");
        System.out.println("0. Save and exit");
        System.out.print("Choice: ");
    }

    private static void addStudent() {
        System.out.print("Student ID: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty() || id.contains(",")) {
            System.out.println("Invalid ID (must not be empty or contain commas).");
            return;
        }
        if (students.containsKey(id)) {
            System.out.println("A student with that ID already exists.");
            return;
        }
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty() || name.contains(",")) {
            System.out.println("Invalid name (must not be empty or contain commas).");
            return;
        }
        students.put(id, new Student(id, name));
        System.out.println("Student added.");
    }

    private static void markAttendance() {
        if (students.isEmpty()) {
            System.out.println("No students yet. Add a student first.");
            return;
        }
        LocalDate date = readDate("Date (yyyy-MM-dd, blank = today): ", true);
        if (date == null) return;

        System.out.println("Marking attendance for " + date
                + "  [P = Present, A = Absent, L = Late, Enter = skip]");
        for (Student s : students.values()) {
            System.out.print(s.getId() + " - " + s.getName() + ": ");
            String in = sc.nextLine().trim().toUpperCase();
            switch (in) {
                case "P": s.getRecords().put(date, Status.PRESENT); break;
                case "A": s.getRecords().put(date, Status.ABSENT); break;
                case "L": s.getRecords().put(date, Status.LATE); break;
                default: break;
            }
        }
        System.out.println("Attendance recorded.");
    }

    private static void viewStudentRecord() {
        Student s = findStudent();
        if (s == null) return;

        System.out.println("\nRecord for " + s.getName() + " (" + s.getId() + ")");
        if (s.getRecords().isEmpty()) {
            System.out.println("No attendance recorded.");
            return;
        }
        for (Map.Entry<LocalDate, Status> e : s.getRecords().entrySet()) {
            System.out.printf("  %s  %s%n", e.getKey(), e.getValue());
        }
    }

    private static void viewByDate() {
        LocalDate date = readDate("Date (yyyy-MM-dd): ", false);
        if (date == null) return;

        System.out.println("\nAttendance on " + date);
        boolean any = false;
        for (Student s : students.values()) {
            Status st = s.getRecords().get(date);
            if (st != null) {
                System.out.printf("  %-10s %-25s %s%n", s.getId(), s.getName(), st);
                any = true;
            }
        }
        if (!any) System.out.println("No records for that date.");
    }

    private static void viewSummary() {
        if (students.isEmpty()) {
            System.out.println("No students to report.");
            return;
        }
        System.out.printf("%n%-10s %-25s %8s %8s %6s %8s%n",
                "ID", "Name", "Present", "Absent", "Late", "Rate");
        for (Student s : students.values()) {
            int p = s.count(Status.PRESENT);
            int a = s.count(Status.ABSENT);
            int l = s.count(Status.LATE);
            int total = p + a + l;

            double rate = total == 0 ? 0 : ((p + l) * 100.0) / total;
            System.out.printf("%-10s %-25s %8d %8d %6d %7.1f%%%n",
                    s.getId(), s.getName(), p, a, l, rate);
        }
    }

    private static void removeStudent() {
        Student s = findStudent();
        if (s == null) return;
        students.remove(s.getId());
        System.out.println("Student removed.");
    }

    private static Student findStudent() {
        System.out.print("Student ID: ");
        String id = sc.nextLine().trim();
        Student s = students.get(id);
        if (s == null) System.out.println("Student not found.");
        return s;
    }

    private static LocalDate readDate(String prompt, boolean allowToday) {
        System.out.print(prompt);
        String in = sc.nextLine().trim();
        if (in.isEmpty() && allowToday) return LocalDate.now();
        try {
            return LocalDate.parse(in);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-MM-dd.");
            return null;
        }
    }

    private static void saveData() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(DATA_FILE)))) {
            for (Student s : students.values()) {
                out.println("S," + s.getId() + "," + s.getName());
            }
            for (Student s : students.values()) {
                for (Map.Entry<LocalDate, Status> e : s.getRecords().entrySet()) {
                    out.println("R," + s.getId() + "," + e.getKey() + "," + e.getValue());
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            List<String[]> recordLines = new ArrayList<>();
            while ((line = in.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 3 && p[0].equals("S")) {
                    students.put(p[1], new Student(p[1], p[2]));
                } else if (p.length == 4 && p[0].equals("R")) {
                    recordLines.add(p);
                }
            }
            for (String[] p : recordLines) {
                Student s = students.get(p[1]);
                if (s != null) {
                    s.getRecords().put(LocalDate.parse(p[2]), Status.valueOf(p[3]));
                }
            }
            System.out.println("Loaded " + students.size() + " student(s) from " + DATA_FILE);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}
