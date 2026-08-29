import java.util.HashMap;
import java.util.Scanner;

public class LoginSystem {

    static Scanner scanner = new Scanner(System.in);

    // Stores username and password
    static HashMap<String, String> users = new HashMap<>();

    public static void main(String[] args) {

        // Default account
        users.put("admin", "1234");

        boolean running = true;

        while (running) {
            System.out.println("\n==========================");
            System.out.println("     LOGIN SYSTEM");
            System.out.println("==========================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.println("==========================");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    login();
                    break;

                case "2":
                    register();
                    break;

                case "3":
                    System.out.println("Exiting system...");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }

    public static void login() {
        System.out.println("\n--- LOGIN ---");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (users.containsKey(username)
                && users.get(username).equals(password)) {

            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + username + "!");

            dashboard(username);

        } else {
            System.out.println("\nInvalid username or password.");
        }
    }

    public static void register() {
        System.out.println("\n--- REGISTER ---");

        System.out.print("Create username: ");
        String username = scanner.nextLine();

        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Create password: ");
        String password = scanner.nextLine();

        users.put(username, password);

        System.out.println("\nRegistration successful!");
        System.out.println("You can now log in.");
    }

    public static void dashboard(String username) {

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n==========================");
            System.out.println("        DASHBOARD");
            System.out.println("==========================");
            System.out.println("Welcome, " + username);
            System.out.println("1. View Profile");
            System.out.println("2. Logout");
            System.out.println("==========================");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n--- PROFILE ---");
                    System.out.println("Username: " + username);
                    break;

                case "2":
                    System.out.println("\nLogged out successfully.");
                    loggedIn = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}