package sms.exception;

/** Thrown when creating a record that would duplicate an existing one. */
public class DuplicateEntityException extends SmsException {
    private static final long serialVersionUID = 1L;

    public DuplicateEntityException(String message) {
        super(message);
    }
}
