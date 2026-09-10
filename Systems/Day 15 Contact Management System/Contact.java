
public class Contact {


    private static int nextId = 1;

    private final int id;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;

    public Contact(String name, String phoneNumber, String email, String address) {
        this.id = nextId++;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return String.format(
                "ID: %-4d Name: %-20s Phone: %-15s Email: %-25s Address: %s",
                id, name, phoneNumber, email, address
        );
    }
}
