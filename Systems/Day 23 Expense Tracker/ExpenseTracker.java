import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ExpenseTracker {
    private static final String FILE_NAME = "expenses.csv";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMMM yyyy");

    private static final List<Expense> expenses = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    static class Expense {
        LocalDate date;
        String category;
        double amount;
        String description;

        Expense(LocalDate date, String category, double amount, String description) {
            this.date = date;
            this.category = category;
            this.amount = amount;
            this.description = description;
        }

        String toCsv() {
            return date.format(DATE_FMT) + "," + category + "," + amount + "," + description;
        }

        static Expense fromCsv(String line) {
            String[] parts = line.split(",", 4);
            if (parts.length < 4) {
                throw new IllegalArgumentException("Missing fields");
            }
            LocalDate date = LocalDate.parse(parts[0].trim(), DATE_FMT);
            String category = parts[1].trim();
            double amount = Double.parseDouble(parts[2].trim());
            String description = parts[3].trim();
            return new Expense(date, category, amount, description);
        }
    }

    public static void main(String[] args) {
        loadFromFile();

        while (true) {
            System.out.println("\n===== EXPENSE TRACKER =====");
            System.out.println("Today: " + LocalDate.now().format(DATE_FMT));
            System.out.println("1. Add expense");
            System.out.println("2. View all expenses");
            System.out.println("3. View expenses by date range");
            System.out.println("4. Monthly summary");
            System.out.println("5. Delete expense");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addExpense(); break;
                case "2": viewAll(); break;
                case "3": viewByDateRange(); break;
                case "4": monthlySummary(); break;
                case "5": deleteExpense(); break;
                case "6":
                    saveToFile();
                    System.out.println("Expenses saved. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1-6.");
            }
        }
    }

    private static void addExpense() {
        System.out.println("\n--- Add Expense ---");
        LocalDate date = readDate("Date (yyyy-MM-dd, blank = today): ", true);
        String category = readNonEmpty("Category (e.g. Food, Transport): ").replace(",", " ");
        double amount = readAmount("Amount: ");
        String description = readNonEmpty("Description: ");

        expenses.add(new Expense(date, category, amount, description));
        saveToFile();
        System.out.println("Expense added and saved.");
    }

    private static void viewAll() {
        System.out.println("\n--- All Expenses ---");
        printTable(expenses);
    }

    private static void viewByDateRange() {
        System.out.println("\n--- Expenses by Date Range ---");
        LocalDate start = readDate("Start date (yyyy-MM-dd): ", false);
        LocalDate end = readDate("End date (yyyy-MM-dd): ", false);

        if (end.isBefore(start)) {
            System.out.println("End date is before start date. Swapping them.");
            LocalDate temp = start;
            start = end;
            end = temp;
        }

        List<Expense> filtered = new ArrayList<>();
        for (Expense e : expenses) {
            if (!e.date.isBefore(start) && !e.date.isAfter(end)) {
                filtered.add(e);
            }
        }
        System.out.println("From " + start.format(DATE_FMT) + " to " + end.format(DATE_FMT) + ":");
        printTable(filtered);
    }

    private static void monthlySummary() {
        System.out.println("\n--- Monthly Summary ---");
        YearMonth month = readMonth("Month (yyyy-MM, blank = this month): ");

        Map<String, Double> byCategory = new TreeMap<>();
        double total = 0;
        int count = 0;

        for (Expense e : expenses) {
            if (YearMonth.from(e.date).equals(month)) {
                byCategory.merge(e.category, e.amount, Double::sum);
                total += e.amount;
                count++;
            }
        }

        System.out.println("Summary for " + month.format(MONTH_FMT));
        if (count == 0) {
            System.out.println("No expenses recorded for this month.");
            return;
        }

        System.out.printf("%-20s %12s%n", "Category", "Total");
        System.out.println("-".repeat(33));
        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            System.out.printf("%-20s %12.2f%n", entry.getKey(), entry.getValue());
        }
        System.out.println("-".repeat(33));
        System.out.printf("%-20s %12.2f%n", "TOTAL", total);
        System.out.printf("Entries: %d | Daily average: %.2f (over %d days)%n",
                count, total / month.lengthOfMonth(), month.lengthOfMonth());
    }

    private static void deleteExpense() {
        System.out.println("\n--- Delete Expense ---");
        if (expenses.isEmpty()) {
            System.out.println("No expenses to delete.");
            return;
        }
        sortByDate();
        printTable(expenses);

        System.out.print("Enter the # to delete (0 to cancel): ");
        try {
            int num = Integer.parseInt(sc.nextLine().trim());
            if (num == 0) {
                System.out.println("Cancelled.");
            } else if (num < 1 || num > expenses.size()) {
                System.out.println("Invalid number.");
            } else {
                Expense removed = expenses.remove(num - 1);
                saveToFile();
                System.out.println("Deleted: " + removed.description);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No saved file found. Starting fresh.");
            return;
        }

        int lineNum = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.isBlank()) continue;
                try {
                    expenses.add(Expense.fromCsv(line));
                } catch (DateTimeParseException | IllegalArgumentException ex) {
                    System.out.println("Skipping invalid line " + lineNum + ": " + line);
                }
            }
            System.out.println("Loaded " + expenses.size() + " expense(s) from " + FILE_NAME + ".");
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage());
        }
    }

    private static void saveToFile() {
        sortByDate();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                writer.write(e.toCsv());
                writer.newLine();
            }
        } catch (IOException ex) {
            System.out.println("Error saving file: " + ex.getMessage());
        }
    }

    private static void sortByDate() {
        expenses.sort(Comparator.comparing((Expense e) -> e.date));
    }

    private static void printTable(List<Expense> list) {
        if (list.isEmpty()) {
            System.out.println("No expenses to show.");
            return;
        }
        list.sort(Comparator.comparing((Expense e) -> e.date));

        System.out.printf("%-4s %-12s %-15s %10s  %s%n", "#", "Date", "Category", "Amount", "Description");
        System.out.println("-".repeat(65));
        double total = 0;
        for (int i = 0; i < list.size(); i++) {
            Expense e = list.get(i);
            System.out.printf("%-4d %-12s %-15s %10.2f  %s%n",
                    i + 1, e.date.format(DATE_FMT), e.category, e.amount, e.description);
            total += e.amount;
        }
        System.out.println("-".repeat(65));
        System.out.printf("%-33s %10.2f%n", "TOTAL", total);
    }

    private static LocalDate readDate(String prompt, boolean blankIsToday) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.isEmpty() && blankIsToday) {
                return LocalDate.now();
            }
            try {
                LocalDate date = LocalDate.parse(input, DATE_FMT);
                if (date.isAfter(LocalDate.now())) {
                    System.out.println("Date cannot be in the future.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date. Use yyyy-MM-dd (e.g. 2026-10-05).");
            }
        }
    }

    private static YearMonth readMonth(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                return YearMonth.now();
            }
            try {
                return YearMonth.parse(input);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid month. Use yyyy-MM (e.g. 2026-10).");
            }
        }
    }

    private static double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double amount = Double.parseDouble(sc.nextLine().trim());
                if (amount <= 0) {
                    System.out.println("Amount must be greater than 0.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid amount. Enter a number (e.g. 150.50).");
            }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty.");
        }
    }
}
