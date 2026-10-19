package sms.model;

import sms.exception.ValidationException;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.OptionalDouble;

/** Holds the component scores (0-100) of one enrollment and computes the weighted grade. */
public class GradeSheet implements Serializable {
    private static final long serialVersionUID = 1L;

    private final EnumMap<GradeComponent, Double> scores = new EnumMap<>(GradeComponent.class);

    public void setScore(GradeComponent component, double score) {
        if (score < 0 || score > 100) {
            throw new ValidationException("Score must be between 0 and 100.");
        }
        scores.put(component, score);
    }

    /** Returns the score, or null if it has not been recorded yet. */
    public Double getScore(GradeComponent component) {
        return scores.get(component);
    }

    public boolean isComplete() {
        return scores.size() == GradeComponent.values().length;
    }

    /** Final weighted grade; present only when every component has a score. */
    public OptionalDouble getFinalGrade() {
        if (!isComplete()) {
            return OptionalDouble.empty();
        }
        double total = 0;
        for (Map.Entry<GradeComponent, Double> entry : scores.entrySet()) {
            total += entry.getValue() * entry.getKey().getWeight();
        }
        return OptionalDouble.of(total);
    }

    /** Weighted average of only the components recorded so far (progress indicator). */
    public OptionalDouble getRunningGrade() {
        if (scores.isEmpty()) {
            return OptionalDouble.empty();
        }
        double total = 0;
        double weights = 0;
        for (Map.Entry<GradeComponent, Double> entry : scores.entrySet()) {
            total += entry.getValue() * entry.getKey().getWeight();
            weights += entry.getKey().getWeight();
        }
        return OptionalDouble.of(total / weights);
    }
}
