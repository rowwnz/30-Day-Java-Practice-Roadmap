package sms.exception;

/** Thrown when an enrollment rule is violated (full course, inactive student, double enrollment, etc.). */
public class EnrollmentException extends SmsException {
    private static final long serialVersionUID = 1L;

    public EnrollmentException(String message) {
        super(message);
    }
}
