import java.util.Scanner;

public class QuizSystem {

    public static void main(String[] args) {
        String[] questions = {
            "Which keyword creates an object in Java?",
            "What is the first index of a Java array?",
            "Which loop is best when the number of repetitions is known?",
            "What does the % operator return?",
            "Which data type stores true or false values?"
        };

        String[][] choices = {
            {"A. new", "B. make", "C. object", "D. create"},
            {"A. 1", "B. -1", "C. 0", "D. It depends on the array"},
            {"A. while", "B. for", "C. do-while", "D. switch"},
            {"A. Quotient", "B. Percentage", "C. Remainder", "D. Decimal"},
            {"A. String", "B. int", "C. char", "D. boolean"}
        };

        char[] correctAnswers = {'A', 'C', 'B', 'C', 'D'};
        Scanner scanner = new Scanner(System.in);
        int score = 0;

        System.out.println("=== Quiz System ===");
        System.out.println("Answer each question using A, B, C, or D.\n");

        for (int questionIndex = 0; questionIndex < questions.length; questionIndex++) {
            System.out.println((questionIndex + 1) + ". " + questions[questionIndex]);

            for (int choiceIndex = 0; choiceIndex < choices[questionIndex].length; choiceIndex++) {
                System.out.println("   " + choices[questionIndex][choiceIndex]);
            }

            char answer = readAnswer(scanner);

            if (answer == correctAnswers[questionIndex]) {
                score++;
                System.out.println("Correct!\n");
            } else {
                System.out.println("Incorrect. The correct answer is "
                        + correctAnswers[questionIndex] + ".\n");
            }
        }

        double percentage = (score * 100.0) / questions.length;
        System.out.println("=== Results ===");
        System.out.println("Score: " + score + " out of " + questions.length);
        System.out.printf("Percentage: %.0f%%%n", percentage);

        if (percentage >= 80) {
            System.out.println("Excellent work!");
        } else if (percentage >= 50) {
            System.out.println("Good effort—keep practicing.");
        } else {
            System.out.println("Review the topics and try again.");
        }

        scanner.close();
    }

    /** Reads a valid multiple-choice response. */
    private static char readAnswer(Scanner scanner) {
        while (true) {
            System.out.print("Your answer: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.length() == 1) {
                char answer = input.charAt(0);
                if (answer >= 'A' && answer <= 'D') {
                    return answer;
                }
            }

            System.out.println("Please enter A, B, C, or D.");
        }
    }
}