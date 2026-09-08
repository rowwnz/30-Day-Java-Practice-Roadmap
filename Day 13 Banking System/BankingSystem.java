import java.util.ArrayList;
import java.util.Scanner;

public class BankingSystem {

    private static ArrayList<BankAccount> accounts = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int nextAccountNumber = 1001;

    public static void main(String[] args) {
        int choice;

        do {
            printMenu();
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    depositToAccount();
                    break;
                case 3:
                    withdrawFromAccount();
                    break;
                case 4:
                    checkBalance();
                    break;
                case 5:
                    displayAllAccounts();
                    break;
                case 6:
                    System.out.println("Exiting Banking System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-6.");
            }
        } while (choice != 6);

        scanner.close();
    }

    // ---- Menu display ----
    private static void printMenu() {
        System.out.println("\n===== BANKING SYSTEM =====");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check Balance");
        System.out.println("5. Display All Accounts");
        System.out.println("6. Exit");
    }

    // ---- Menu actions (each delegates work; no direct field access) ----

    private static void createAccount() {
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();
        double initialDeposit = readDouble("Enter initial deposit amount: ");

        String accountNumber = "ACCT-" + nextAccountNumber++;
        BankAccount account = new BankAccount(accountNumber, name, initialDeposit);
        accounts.add(account);

        System.out.println("Account created successfully!");
        account.displayAccountInfo();
    }

    private static void depositToAccount() {
        BankAccount account = findAccount();
        if (account == null) return;

        double amount = readDouble("Enter deposit amount: ");
        account.deposit(amount);
    }

    private static void withdrawFromAccount() {
        BankAccount account = findAccount();
        if (account == null) return;

        double amount = readDouble("Enter withdrawal amount: ");
        account.withdraw(amount);
    }

    private static void checkBalance() {
        BankAccount account = findAccount();
        if (account == null) return;

        System.out.printf("Current balance: %.2f%n", account.getBalance());
    }

    private static void displayAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        for (BankAccount account : accounts) {
            account.displayAccountInfo();
        }
    }

    // ---- Helper methods ----

    private static BankAccount findAccount() {
        System.out.print("Enter account number: ");
        String accNum = scanner.nextLine();

        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equalsIgnoreCase(accNum)) {
                return account;
            }
        }
        System.out.println("Account not found.");
        return null;
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume leftover newline
        return value;
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // consume leftover newline
        return value;
    }
}
