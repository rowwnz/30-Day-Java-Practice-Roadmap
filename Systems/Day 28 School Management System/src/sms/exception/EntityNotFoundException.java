package sms.exception;

/** Thrown when a record with the given ID does not exist. */
public class EntityNotFoundException extends SmsException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String entityName, String id) {
        super(entityName + " with ID '" + id + "' was not found.");
    }
}
