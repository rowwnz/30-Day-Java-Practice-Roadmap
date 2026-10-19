package sms.util;

import sms.exception.ValidationException;

import java.util.regex.Pattern;

/** Central place for input validation rules. Every method throws ValidationException on bad input. */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern CONTACT = Pattern.compile("^\\+?\\d{7,13}$");
    private static final Pattern COURSE_CODE = Pattern.compile("^[A-Z0-9]{3,10}$");

    private Validator() {
    }

    public static String requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " must not be empty.");
        }
        return value.trim();
    }

    public static String requireEmail(String value) {
        String email = requireNonBlank(value, "Email").toLowerCase();
        if (!EMAIL.matcher(email).matches()) {
            throw new ValidationException("Invalid email address: " + value);
        }
        return email;
    }

    public static String requireContact(String value) {
        String contact = requireNonBlank(value, "Contact number").replace(" ", "").replace("-", "");
        if (!CONTACT.matcher(contact).matches()) {
            throw new ValidationException("Contact number must be 7-13 digits (optional leading +).");
        }
        return contact;
    }

    public static String requireCourseCode(String value) {
        String code = requireNonBlank(value, "Course code").toUpperCase();
        if (!COURSE_CODE.matcher(code).matches()) {
            throw new ValidationException("Course code must be 3-10 letters/digits with no spaces.");
        }
        return code;
    }

    public static int requireRange(int value, int min, int max, String field) {
        if (value < min || value > max) {
            throw new ValidationException(field + " must be between " + min + " and " + max + ".");
        }
        return value;
    }
}
