import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

public class LoginRegistrationSystem {

    private static final String USER_FILE = "users.txt";
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n===== LOGIN & REGISTRATION SYSTEM =====");
            System.out.println("[1] Register");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    register();
                    break;
                case "2":
                    login();
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }
        sc.close();
    }

    private static void register() {
        System.out.println("\n--- REGISTER ---");

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        if (!isValidUsername(username)) {
            System.out.println("Username must be 4-20 characters (letters, digits, underscore only).");
            return;
        }
        if (findUser(username) != null) {
            System.out.println("Username already exists. Please choose another.");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (!isValidPassword(password)) {
            System.out.println("Password must be at least 8 characters and contain a letter and a digit.");
            return;
        }

        System.out.print("Confirm password: ");
        String confirm = sc.nextLine();

        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match.");
            return;
        }

        String salt = generateSalt();
        String hashed = hashPassword(password, salt);

        if (saveUser(username, salt, hashed)) {
            System.out.println("Registration successful! You can now log in.");
        } else {
            System.out.println("Registration failed. Could not write to file.");
        }
    }

    private static void login() {
        System.out.println("\n--- LOGIN ---");

        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("Username: ");
            String username = sc.nextLine().trim();
            System.out.print("Password: ");
            String password = sc.nextLine();

            if (authenticate(username, password)) {
                System.out.println("Login successful. Welcome, " + username + "!");
                userMenu(username);
                return;
            }

            int remaining = MAX_LOGIN_ATTEMPTS - attempt;
            System.out.println("Invalid username or password. Attempts left: " + remaining);
        }
        System.out.println("Too many failed attempts. Returning to main menu.");
    }

    private static boolean authenticate(String username, String password) {
        String[] user = findUser(username);
        if (user == null) {
            return false;
        }
        String salt = user[1];
        String storedHash = user[2];
        return hashPassword(password, salt).equals(storedHash);
    }

    private static void userMenu(String username) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- DASHBOARD (" + username + ") ---");
            System.out.println("[1] Change password");
            System.out.println("[2] Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                changePassword(username);
            } else if (choice.equals("2")) {
                loggedIn = false;
                System.out.println("Logged out.");
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void changePassword(String username) {
        System.out.print("Current password: ");
        String current = sc.nextLine();
        if (!authenticate(username, current)) {
            System.out.println("Incorrect current password.");
            return;
        }

        System.out.print("New password: ");
        String newPass = sc.nextLine();
        if (!isValidPassword(newPass)) {
            System.out.println("Password must be at least 8 characters and contain a letter and a digit.");
            return;
        }

        String salt = generateSalt();
        String hashed = hashPassword(newPass, salt);

        if (updateUser(username, salt, hashed)) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to update password.");
        }
    }

    private static String[] findUser(String username) {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equalsIgnoreCase(username)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user file: " + e.getMessage());
        }
        return null;
    }

    private static boolean saveUser(String username, String salt, String hashed) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(username + "," + salt + "," + hashed);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing user file: " + e.getMessage());
            return false;
        }
    }

    private static boolean updateUser(String username, String newSalt, String newHash) {
        File file = new File(USER_FILE);
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equalsIgnoreCase(username)) {
                    content.append(parts[0]).append(",").append(newSalt).append(",").append(newHash);
                } else {
                    content.append(line);
                }
                content.append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error reading user file: " + e.getMessage());
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(content.toString());
            return true;
        } catch (IOException e) {
            System.out.println("Error writing user file: " + e.getMessage());
            return false;
        }
    }

    private static boolean isValidUsername(String username) {
        return username.matches("[A-Za-z0-9_]{4,20}");
    }

    private static boolean isValidPassword(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return password.length() >= 8 && hasLetter && hasDigit && !password.contains(",");
    }

    private static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return toHex(salt);
    }

    private static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
