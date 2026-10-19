package sms.exception;

/**
 * Base class for every business-rule error in the system.
 * Menus catch this single type and show its message to the user.
 */
public class SmsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SmsException(String message) {
        super(message);
    }
}
