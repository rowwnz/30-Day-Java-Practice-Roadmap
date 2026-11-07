package gym.exception;

/** Thrown when a member, trainer, class, payment or user ID does not exist. */
public class NotFoundException extends GymException {
    public NotFoundException(String message) {
        super(message);
    }
}
