package sms.model;

/**
 * The graded parts of a course and their weights.
 * Change the weights here to match your school's grading breakdown (they must add up to 1.0).
 */
public enum GradeComponent {
    QUIZZES("Quizzes", 0.20),
    INTERIM_EXAM("Interim Exam", 0.20),
    MIDTERM_EXAM("Midterm Exam", 0.30),
    FINAL_EXAM("Final Exam", 0.30);

    private final String label;
    private final double weight;

    GradeComponent(String label, double weight) {
        this.label = label;
        this.weight = weight;
    }

    public String getLabel() {
        return label;
    }

    public double getWeight() {
        return weight;
    }

    /** Short column header used in tables. */
    public String getShortLabel() {
        switch (this) {
            case QUIZZES:
                return "Quiz";
            case INTERIM_EXAM:
                return "Interim";
            case MIDTERM_EXAM:
                return "Midterm";
            default:
                return "Final";
        }
    }

    @Override
    public String toString() {
        return label + " (" + Math.round(weight * 100) + "%)";
    }
}
