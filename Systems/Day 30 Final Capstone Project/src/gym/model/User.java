package gym.model;

import gym.util.Csv;

/** A system login account (not a gym member). */
public class User implements Storable {
    private final String username;
    private String passwordHash;
    private final Role role;
    private boolean mustChangePassword;

    public User(String username, String passwordHash, Role role, boolean mustChangePassword) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.mustChangePassword = mustChangePassword;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean mustChangePassword() { return mustChangePassword; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    public void setPassword(String newHash, boolean mustChangeNextLogin) {
        this.passwordHash = newHash;
        this.mustChangePassword = mustChangeNextLogin;
    }

    @Override
    public String toCsv() {
        return Csv.join(username, passwordHash, role, mustChangePassword);
    }

    public static User fromCsv(String[] f) {
        return new User(f[0], f[1], Role.valueOf(f[2]), Boolean.parseBoolean(f[3]));
    }
}
