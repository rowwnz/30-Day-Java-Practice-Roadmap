package sms.model;

public class Teacher extends Person {
    private static final long serialVersionUID = 1L;

    private String department;
    private String specialization;

    public Teacher(String id, String firstName, String lastName, String email, String contactNumber,
                   String department, String specialization) {
        super(id, firstName, lastName, email, contactNumber);
        this.department = department;
        this.specialization = specialization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String getRole() {
        return "Teacher";
    }

    @Override
    protected String getProfileDetails() {
        return String.format("Department : %s%n", department)
                + String.format("Specialty  : %s%n", specialization);
    }
}
