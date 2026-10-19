package sms.repository;

import sms.model.Identifiable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/** Repository backed by a LinkedHashMap. IDs are matched case-insensitively. */
public class InMemoryRepository<T extends Identifiable> implements Repository<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, T> store = new LinkedHashMap<>();

    private static String key(String id) {
        return id == null ? "" : id.trim().toUpperCase();
    }

    @Override
    public void save(T entity) {
        store.put(key(entity.getId()), entity);
    }

    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(store.get(key(id)));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<T> findBy(Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T entity : store.values()) {
            if (condition.test(entity)) {
                result.add(entity);
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(key(id));
    }

    @Override
    public boolean deleteById(String id) {
        return store.remove(key(id)) != null;
    }

    @Override
    public int count() {
        return store.size();
    }
}
