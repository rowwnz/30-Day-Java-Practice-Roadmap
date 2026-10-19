package sms.model;

/**
 * NU Grade Point scale. A percentage grade is rounded to the nearest whole number,
 * then matched to the first band whose minimum it reaches.
 */
public enum GradeScale {
    EXCELLENT(4.0, "4.0", "Excellent", 96),
    VERY_GOOD(3.5, "3.5", "Very Good", 90),
    GOOD(3.0, "3.0", "Good", 84),
    ABOVE_SATISFACTORY(2.5, "2.5", "Above Satisfactory", 78),
    SATISFACTORY(2.0, "2.0", "Satisfactory", 72),
    FAIR(1.5, "1.5", "Fair", 66),
    PASS(1.0, "1.0", "Pass", 60),
    REPEAT(0.0, "R", "Repeat", 0);

    private final double gradePoint;
    private final String label;
    private final String description;
    private final int minScore;

    GradeScale(double gradePoint, String label, String description, int minScore) {
        this.gradePoint = gradePoint;
        this.label = label;
        this.description = description;
        this.minScore = minScore;
    }

    public static GradeScale fromScore(double score) {
        long rounded = Math.round(score);
        for (GradeScale grade : values()) {
            if (rounded >= grade.minScore) {
                return grade;
            }
        }
        return REPEAT;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getMinScore() {
        return minScore;
    }

    /** Upper bound of the band, used when printing the scale. */
    public int getMaxScore() {
        int index = ordinal();
        return index == 0 ? 100 : values()[index - 1].minScore - 1;
    }

    public boolean isPassing() {
        return this != REPEAT;
    }
}
