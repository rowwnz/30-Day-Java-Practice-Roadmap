package sms.repository;

import sms.model.Identifiable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Generic data-access contract. Services depend on this interface, not on a concrete
 * storage class, so the storage can be swapped (e.g. for a database) without touching them.
 */
public interface Repository<T extends Identifiable> {

    void save(T entity);

    Optional<T> findById(String id);

    List<T> findAll();

    List<T> findBy(Predicate<T> condition);

    boolean existsById(String id);

    boolean deleteById(String id);

    int count();
}
