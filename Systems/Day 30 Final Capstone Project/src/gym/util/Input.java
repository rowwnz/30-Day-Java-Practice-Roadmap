package gym.util;

import java.io.Console;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** All keyboard input goes through here; every prompt re-asks until the value is valid. */
public final class Input {
    private static final Scanner SCANNER = new Scanner(System.in);

    private Input() { }

    public static String line(String prompt) {
        System.out.print("  " + prompt);
        return SCANNER.nextLine().trim();
    }

    public static String required(String prompt) {
        while (true) {
            String value = line(prompt);
            if (!value.isEmpty()) return value;
            System.out.println("  This field is required.");
        }
    }

    /** Shows the current value in brackets; pressing Enter keeps it. */
    public static String optional(String label, String current) {
        String value = line(label + " [" + current + "]: ");
        return value.isEmpty() ? current : value;
    }

    public static int intInRange(String prompt, int min, int max) {
        while (true) {
            String value = line(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) return number;
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.printf("  Enter a whole number from %d to %d.%n", min, max);
        }
    }

    public static double money(String prompt, double min, double max) {
        while (true) {
            String value = line(prompt).replace(",", "");
            try {
                double amount = Double.parseDouble(value);
                if (amount >= min && amount <= max) return Fmt.round2(amount);
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.printf("  Enter an amount from %s to %s.%n", Fmt.peso(min), Fmt.peso(max));
        }
    }

    public static boolean confirm(String prompt) {
        while (true) {
            String value = line(prompt + " (y/n): ").toLowerCase();
            if (value.equals("y") || value.equals("yes")) return true;
            if (value.equals("n") || value.equals("no")) return false;
            System.out.println("  Please answer y or n.");
        }
    }

    /** Blank input returns the default date. */
    public static LocalDate date(String prompt, LocalDate defaultValue) {
        while (true) {
            String value = line(prompt + " (yyyy-mm-dd, Enter = " + defaultValue + "): ");
            if (value.isEmpty()) return defaultValue;
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid date. Example: 2026-10-27");
            }
        }
    }

    public static LocalTime time(String prompt) {
        while (true) {
            String value = line(prompt + " (HH:mm, 24-hour): ");
            if (value.matches("\\d:\\d\\d")) value = "0" + value;   // accept 6:30 as 06:30
            try {
                return LocalTime.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid time. Example: 18:30");
            }
        }
    }

    public static <E extends Enum<E>> E choose(String prompt, E[] values) {
        for (int i = 0; i < values.length; i++) {
            System.out.printf("    %d. %s%n", i + 1, values[i]);
        }
        return values[intInRange(prompt, 1, values.length) - 1];
    }

    public static String name(String prompt) {
        while (true) {
            String value = required(prompt).replaceAll("\\s+", " ");
            if (Validator.isName(value)) return value;
            System.out.println("  Use letters, spaces, periods, apostrophes or hyphens only (2-60 characters).");
        }
    }

    public static String mobile(String prompt) {
        while (true) {
            String value = Validator.normalizeMobile(required(prompt));
            if (Validator.isMobile(value)) return value;
            System.out.println("  Enter an 11-digit mobile number starting with 09, e.g. 09171234567.");
        }
    }

    /** Email is optional: blank is allowed, anything else must look like an address. */
    public static String email(String prompt) {
        while (true) {
            String value = line(prompt);
            if (value.isEmpty() || Validator.isEmail(value)) return value;
            System.out.println("  Invalid email address. Leave it blank to skip.");
        }
    }

    /** Hides typing in a real terminal; inside an IDE console the password is visible. */
    public static String password(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] chars = console.readPassword("  " + prompt);
            if (chars == null) throw new NoSuchElementException("Input closed");
            return new String(chars);
        }
        return line(prompt);
    }

    public static void pause() {
        line("Press Enter to continue...");
    }
}
