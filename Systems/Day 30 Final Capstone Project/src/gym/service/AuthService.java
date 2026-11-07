package gym.service;

import gym.exception.GymException;
import gym.exception.NotFoundException;
import gym.model.Role;
import gym.model.User;
import gym.storage.GymData;
import gym.util.PasswordHasher;
import gym.util.Validator;

import java.util.Comparator;
import java.util.List;

/** Login, registration and password rules for system accounts. */
public class AuthService {
    public static final int MAX_LOGIN_ATTEMPTS = 3;

    private final GymData data;

    public AuthService(GymData data) {
        this.data = data;
    }

    public User authenticate(String username, String password) {
        User user = data.getUsers().get(username.trim().toLowerCase());
        // Same message for unknown user and wrong password, so usernames cannot be guessed
        if (user == null || !PasswordHasher.verify(password, user.getPasswordHash())) {
            throw new GymException("Invalid username or password.");
        }
        return user;
    }

    /** New accounts get a temporary password that must be changed at first login. */
    public User register(String username, String temporaryPassword, Role role) {
        String name = username.trim().toLowerCase();
        Validator.require(Validator.isUsername(name),
                "Username must be 4-20 characters: lowercase letters, digits or _, starting with a letter.");
        Validator.require(!data.getUsers().containsKey(name), "Username '" + name + "' is already taken.");
        requireStrong(temporaryPassword);
        User user = new User(name, PasswordHasher.hash(temporaryPassword), role, true);
        data.getUsers().put(name, user);
        return user;
    }

    public void changePassword(User user, String currentPassword, String newPassword) {
        Validator.require(PasswordHasher.verify(currentPassword, user.getPasswordHash()), "Current password is incorrect.");
        Validator.require(!currentPassword.equals(newPassword), "The new password must be different from the current one.");
        requireStrong(newPassword);
        user.setPassword(PasswordHasher.hash(newPassword), false);
    }

    public void resetPassword(String username, String temporaryPassword) {
        User user = find(username);
        requireStrong(temporaryPassword);
        user.setPassword(PasswordHasher.hash(temporaryPassword), true);
    }

    public void delete(User actor, String username) {
        User target = find(username);
        Validator.require(!target.getUsername().equals(actor.getUsername()), "You cannot delete your own account.");
        if (target.isAdmin()) {
            long admins = data.getUsers().values().stream().filter(User::isAdmin).count();
            Validator.require(admins > 1, "Cannot delete the last administrator account.");
        }
        data.getUsers().remove(target.getUsername());
    }

    public User find(String username) {
        User user = data.getUsers().get(username.trim().toLowerCase());
        if (user == null) throw new NotFoundException("No user account named '" + username + "'.");
        return user;
    }

    public List<User> list() {
        return data.getUsers().values().stream()
                .sorted(Comparator.comparing(User::getRole).thenComparing(User::getUsername))
                .toList();
    }

    private static void requireStrong(String password) {
        String problem = Validator.passwordProblem(password);
        Validator.require(problem == null, problem);
    }
}
