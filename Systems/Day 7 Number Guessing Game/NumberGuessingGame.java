import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    public static void main(String[] args) {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        int secretNumber = random.nextInt(100) + 1;
        int guess = 0;
        int attempts = 0;

        System.out.println("=== Number Guessing Game ===");
        System.out.println("I picked a number from 1 to 100.");

        while (guess != secretNumber) {
            System.out.print("Enter your guess: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter a whole number.");
                scanner.next();
                continue;
            }

            guess = scanner.nextInt();
            attempts++;

            if (guess < 1 || guess > 100) {
                System.out.println("Your guess must be between 1 and 100.");
            } else if (guess < secretNumber) {
                System.out.println("Too low. Try again!");
            } else if (guess > secretNumber) {
                System.out.println("Too high. Try again!");
            } else {
                System.out.println("Correct! You guessed the number in " + attempts + " attempts.");
            }
        }

        scanner.close();
    }
}