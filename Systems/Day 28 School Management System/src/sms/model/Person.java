package sms.model;

import java.io.Serializable;

/**
 * Abstract base class for every person in the system.
 * Demonstrates abstraction, encapsulation, inheritance and the Template Method pattern
 * (getProfile() is fixed here; getProfileDetails() is filled in by each subclass).
 */
public abstract class Person implements Identifiable, Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;

    protected Person(String id, String firstName, String lastName, String email, String contactNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getFullName() {
        return lastName + ", " + firstName;
    }

    /** Each subclass states its own role (polymorphism). */
    public abstract String getRole();

    /** Role-specific lines appended to the common profile. */
    protected abstract String getProfileDetails();

    /** Template method: common fields first, then the subclass-specific details. */
    public final String getProfile() {
        return String.format("ID         : %s%n", id)
                + String.format("Name       : %s%n", getFullName())
                + String.format("Role       : %s%n", getRole())
                + String.format("Email      : %s%n", email)
                + String.format("Contact No.: %s%n", contactNumber)
                + getProfileDetails();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", id, getFullName(), getRole());
    }
}
