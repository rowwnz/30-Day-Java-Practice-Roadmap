import java.util.Scanner;

public class ATMSimulation {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        double balance = 5000.00;
        boolean running = true;

        while (running) {

            System.out.println("\n--- ATM MENU ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            // Validate menu input
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number from 1 to 4.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();

            switch (choice) {

                // CHECK BALANCE
                case 1:
                    System.out.printf(
                        "Your current balance is: \u20B1%.2f%n",
                        balance
                    );
                    break;

                // DEPOSIT
                case 2:
                    System.out.print("Enter deposit amount: \u20B1");

                    if (!scanner.hasNextDouble()) {
                        System.out.println("Invalid amount.");
                        scanner.next();
                        break;
                    }

                    double deposit = scanner.nextDouble();

                    if (deposit <= 0) {
                        System.out.println(
                            "Deposit amount must be greater than zero."
                        );
                    } else {
                        balance += deposit;

                        System.out.printf(
                            "Deposit successful! New balance: \u20B1%.2f%n",
                            balance
                        );
                    }
                    break;

                // WITHDRAW
                case 3:
                    System.out.print("Enter withdrawal amount: \u20B1");

                    if (!scanner.hasNextDouble()) {
                        System.out.println("Invalid amount.");
                        scanner.next();
                        break;
                    }

                    double withdrawal = scanner.nextDouble();

                    if (withdrawal <= 0) {
                        System.out.println(
                            "Withdrawal amount must be greater than zero."
                        );

                    } else if (withdrawal > balance) {
                        System.out.println("Insufficient balance.");

                    } else {
                        balance -= withdrawal;

                        System.out.printf(
                            "Withdrawal successful! New balance: \u20B1%.2f%n",
                            balance
                        );
                    }
                    break;

                // EXIT
                case 4:
                    System.out.println("Thank you for using the ATM!");
                    running = false;
                    break;

                // INVALID MENU CHOICE
                default:
                    System.out.println(
                        "Invalid choice. Please select from 1 to 4."
                    );
            }
        }

        scanner.close();
    }
}