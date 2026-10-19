package sms.model;

public class Student extends Person {
    private static final long serialVersionUID = 1L;

    private String program;
    private int yearLevel;
    private StudentStatus status;

    public Student(String id, String firstName, String lastName, String email, String contactNumber,
                   String program, int yearLevel) {
        super(id, firstName, lastName, email, contactNumber);
        this.program = program;
        this.yearLevel = yearLevel;
        this.status = StudentStatus.ACTIVE;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    protected String getProfileDetails() {
        return String.format("Program    : %s%n", program)
                + String.format("Year Level : %d%n", yearLevel)
                + String.format("Status     : %s%n", status);
    }
}
