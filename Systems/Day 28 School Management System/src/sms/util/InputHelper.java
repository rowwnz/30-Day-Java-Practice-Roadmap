package sms.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Wraps Scanner with prompts that keep asking until the input is valid. */
public class InputHelper {
    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  This field is required.");
        }
    }

    /** Shows the current value; returns null if the user just presses Enter (keep current). */
    public String readOptional(String label, String current) {
        String value = readLine(label + " [" + current + "]: ");
        return value.isEmpty() ? null : value;
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readLine(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("  Enter a whole number from " + min + " to " + max + ".");
        }
    }

    /** Returns null if the user presses Enter (keep current). */
    public Integer readOptionalInt(String label, int current, int min, int max) {
        while (true) {
            String value = readLine(label + " [" + current + "]: ");
            if (value.isEmpty()) {
                return null;
            }
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // fall through
            }
            System.out.println("  Enter a whole number from " + min + " to " + max + ", or press Enter to keep.");
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            String value = readLine(prompt);
            try {
                double number = Double.parseDouble(value);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // fall through
            }
            System.out.println("  Enter a number from " + min + " to " + max + ".");
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String value = readLine(prompt + " (YYYY-MM-DD, Enter = today): ");
            if (value.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid date. Example: 2026-10-19");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt + " (y/n): ").toLowerCase();
            if (value.equals("y") || value.equals("yes")) {
                return true;
            }
            if (value.equals("n") || value.equals("no")) {
                return false;
            }
            System.out.println("  Please answer y or n.");
        }
    }

    /** Lists the enum constants as a numbered menu and returns the chosen one. */
    public <E extends Enum<E>> E readEnum(String prompt, Class<E> type) {
        E[] values = type.getEnumConstants();
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        return values[readInt(prompt, 1, values.length) - 1];
    }

    public void pause() {
        readLine("\nPress Enter to continue...");
    }
}
