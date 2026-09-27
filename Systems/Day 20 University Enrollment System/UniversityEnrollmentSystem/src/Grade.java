public class Grade {

    private final double gradePoint;
    private final String remarks;

    public Grade(double gradePoint, String remarks) {
        this.gradePoint = gradePoint;
        this.remarks = remarks;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public String toString() {
        return String.format("%.2f (%s)", gradePoint, remarks);
    }
}
