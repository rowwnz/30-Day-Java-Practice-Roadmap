import java.util.Scanner;

public class GradeCalculator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter your grade: ");
        int grade = input.nextInt();

        if (grade >= 90) {
            System.out.println("Excellent");
        } else if (grade >= 80) {
            System.out.println("Good");
        } else if (grade >= 75) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }

        input.close();
    }
}