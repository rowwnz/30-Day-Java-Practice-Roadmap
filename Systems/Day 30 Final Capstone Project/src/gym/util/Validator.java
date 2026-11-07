package gym.util;

import gym.exception.ValidationException;

import java.util.regex.Pattern;

/** Format checks shared by the input layer and the services. */
public final class Validator {
    private static final Pattern NAME = Pattern.compile("^\\p{L}[\\p{L} .'-]{1,59}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern MOBILE = Pattern.compile("^09\\d{9}$");
    private static final Pattern USERNAME = Pattern.compile("^[a-z][a-z0-9_]{3,19}$");

    private Validator() { }

    public static boolean isName(String s) {
        return s != null && NAME.matcher(s).matches();
    }

    public static boolean isEmail(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }

    public static boolean isMobile(String s) {
        return s != null && MOBILE.matcher(s).matches();
    }

    public static boolean isUsername(String s) {
        return s != null && USERNAME.matcher(s).matches();
    }

    /** Accepts "0917 123 4567", "0917-123-4567" or "+639171234567" and returns "09171234567". */
    public static String normalizeMobile(String raw) {
        String digits = raw.replaceAll("[\\s-]", "");
        if (digits.startsWith("+63")) digits = "0" + digits.substring(3);
        return digits;
    }

    /** Returns why a password is too weak, or null if it is acceptable. */
    public static String passwordProblem(String password) {
        if (password.length() < 8) return "Password must be at least 8 characters.";
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            return "Password must contain both letters and numbers.";
        }
        return null;
    }

    public static void require(boolean condition, String message) {
        if (!condition) throw new ValidationException(message);
    }
}
