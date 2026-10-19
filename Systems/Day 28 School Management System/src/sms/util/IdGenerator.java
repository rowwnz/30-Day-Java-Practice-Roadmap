package sms.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Produces sequential IDs per prefix, e.g. S-0001, T-0001, E-00001. Saved with the data. */
public class IdGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> counters = new HashMap<>();

    public String next(String prefix, int width) {
        Integer current = counters.get(prefix);
        int next = (current == null ? 0 : current) + 1;
        counters.put(prefix, next);
        return prefix + "-" + String.format("%0" + width + "d", next);
    }
}
