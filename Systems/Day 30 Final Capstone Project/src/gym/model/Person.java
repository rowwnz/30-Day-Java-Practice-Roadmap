package gym.model;

/** Contact data shared by members and trainers. */
public abstract class Person implements Storable {
    private final String id;
    private String fullName;
    private String phone;
    private String email;

    protected Person(String id, String fullName, String phone, String email) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email == null ? "" : email;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email == null ? "" : email; }

    /** Human-readable role, e.g. "Student Member" or "Trainer". */
    public abstract String getRoleLabel();

    @Override
    public String toString() {
        return id + " - " + fullName;
    }
}
