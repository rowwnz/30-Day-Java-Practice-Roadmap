package gym.exception;

/**
 * Base class for every business-rule error in the system.
 * Unchecked so services stay clean; the UI catches it once in Menu.show().
 */
public class GymException extends RuntimeException {
    public GymException(String message) {
        super(message);
    }
}
