package sms.service;

import sms.exception.DuplicateEntityException;
import sms.exception.EntityNotFoundException;
import sms.model.Person;
import sms.repository.Repository;
import sms.util.IdGenerator;
import sms.util.Validator;

import java.util.Comparator;
import java.util.List;

/**
 * Shared logic for any service that manages a Person subtype.
 * StudentService and TeacherService inherit lookup, search, sorting and the
 * common update rules, and only add what is specific to them.
 */
public abstract class PersonService<T extends Person> {
    protected final Repository<T> repository;
    protected final IdGenerator ids;

    protected PersonService(Repository<T> repository, IdGenerator ids) {
        this.repository = repository;
        this.ids = ids;
    }

    /** "Student", "Teacher", ... used in messages. */
    protected abstract String entityName();

    public T getById(String id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(entityName(), id));
    }

    public List<T> getAll() {
        List<T> all = repository.findAll();
        all.sort(Comparator.comparing((T p) -> p.getLastName(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(p -> p.getFirstName(), String.CASE_INSENSITIVE_ORDER));
        return all;
    }

    public List<T> search(String keyword) {
        String key = keyword.trim().toLowerCase();
        List<T> result = repository.findBy(p ->
                p.getFullName().toLowerCase().contains(key)
                        || (p.getFirstName() + " " + p.getLastName()).toLowerCase().contains(key)
                        || p.getId().toLowerCase().contains(key));
        result.sort(Comparator.comparing((T p) -> p.getLastName(), String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public int count() {
        return repository.count();
    }

    protected void ensureEmailUnique(String email, String excludeId) {
        boolean taken = !repository.findBy(p -> p.getEmail().equalsIgnoreCase(email)
                && (excludeId == null || !p.getId().equalsIgnoreCase(excludeId))).isEmpty();
        if (taken) {
            throw new DuplicateEntityException("A " + entityName().toLowerCase()
                    + " with email " + email + " already exists.");
        }
    }

    /** Applies the fields every Person has. A null argument means "keep the current value". */
    protected void applyCommonUpdates(T person, String firstName, String lastName, String email, String contact) {
        if (firstName != null) {
            person.setFirstName(Validator.requireNonBlank(firstName, "First name"));
        }
        if (lastName != null) {
            person.setLastName(Validator.requireNonBlank(lastName, "Last name"));
        }
        if (email != null) {
            String cleanEmail = Validator.requireEmail(email);
            ensureEmailUnique(cleanEmail, person.getId());
            person.setEmail(cleanEmail);
        }
        if (contact != null) {
            person.setContactNumber(Validator.requireContact(contact));
        }
    }
}
