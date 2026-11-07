package gym.model;

import gym.util.Csv;

public class Trainer extends Person {
    private String specialty;

    public Trainer(String id, String fullName, String phone, String email, String specialty) {
        super(id, fullName, phone, email);
        this.specialty = specialty;
    }

    public String getSpecialty() { return specialty; }

    public void setSpecialty(String specialty) { this.specialty = specialty; }

    @Override
    public String getRoleLabel() { return "Trainer"; }

    @Override
    public String toCsv() {
        return Csv.join(getId(), getFullName(), getPhone(), getEmail(), specialty);
    }

    public static Trainer fromCsv(String[] f) {
        return new Trainer(f[0], f[1], f[2], f[3], f[4]);
    }
}
