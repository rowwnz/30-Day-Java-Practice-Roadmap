package gym.storage;

import gym.model.CheckIn;
import gym.model.FitnessClass;
import gym.model.Member;
import gym.model.Payment;
import gym.model.Trainer;
import gym.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The in-memory database: every record the system knows about, keyed by ID. */
public class GymData {
    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Member> members = new LinkedHashMap<>();
    private final Map<String, Trainer> trainers = new LinkedHashMap<>();
    private final Map<String, FitnessClass> classes = new LinkedHashMap<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<CheckIn> checkIns = new ArrayList<>();
    private final Map<String, Integer> sequences = new HashMap<>();

    public Map<String, User> getUsers() { return users; }
    public Map<String, Member> getMembers() { return members; }
    public Map<String, Trainer> getTrainers() { return trainers; }
    public Map<String, FitnessClass> getClasses() { return classes; }
    public List<Payment> getPayments() { return payments; }
    public List<CheckIn> getCheckIns() { return checkIns; }
    Map<String, Integer> getSequences() { return sequences; }

    /**
     * Returns the next ID for a prefix, e.g. nextId("M", 4) gives "M-0007".
     * Counters are saved to disk, so IDs of deleted records are never reused.
     */
    public String nextId(String prefix, int width) {
        int next = sequences.merge(prefix, 1, Integer::sum);
        return prefix + "-" + String.format("%0" + width + "d", next);
    }

    void ensureSequenceAtLeast(String prefix, int value) {
        sequences.merge(prefix, value, Math::max);
    }
}
