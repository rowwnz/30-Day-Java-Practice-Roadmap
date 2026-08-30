import java.util.Scanner;

public class StudentGradeSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of students: ");
        int numberOfStudents = scanner.nextInt();

        // Loop through each student
        for (int i = 1; i <= numberOfStudents; i++) {

            System.out.println("\n--- Student " + i + " ---");

            System.out.print("Enter student name: ");
            scanner.nextLine(); // Clear buffer
            String name = scanner.nextLine();

            double total = 0;

            // Loop for 3 subjects
            for (int subject = 1; subject <= 3; subject++) {
                System.out.print("Enter grade for Subject " + subject + ": ");
                double grade = scanner.nextDouble();

                total += grade;
            }

            // Calculate average
            double average = total / 3;

            // Determine status using conditions
            String status;

            if (average >= 75) {
                status = "PASSED";
            } else {
                status = "FAILED";
            }

            // Display results
            System.out.println("\nStudent Name: " + name);
            System.out.println("Average: " + average);
            System.out.println("Status: " + status);
        }

        System.out.println("\nAll student grades have been processed.");

        scanner.close();
    }
}