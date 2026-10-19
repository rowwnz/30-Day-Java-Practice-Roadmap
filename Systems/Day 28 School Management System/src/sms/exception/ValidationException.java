package sms.exception;

/** Thrown when input data is invalid (blank names, bad email, out-of-range values, etc.). */
public class ValidationException extends SmsException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
