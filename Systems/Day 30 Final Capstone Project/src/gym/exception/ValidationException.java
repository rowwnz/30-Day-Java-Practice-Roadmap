package gym.exception;

/** Thrown when input breaks a business rule (bad format, duplicate, over capacity, ...). */
public class ValidationException extends GymException {
    public ValidationException(String message) {
        super(message);
    }
}
