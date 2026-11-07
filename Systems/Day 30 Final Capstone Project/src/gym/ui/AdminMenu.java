package gym.ui;

import gym.AppContext;
import gym.exception.GymException;
import gym.model.Role;
import gym.model.User;
import gym.util.Input;
import gym.util.Table;

import java.io.IOException;
import java.util.List;

/** Admin-only: manage login accounts and read the activity log. */
public class AdminMenu extends Menu {

    public AdminMenu(AppContext ctx) {
        super(ctx, "USER ACCOUNTS & ACTIVITY LOG");
    }

    @Override
    protected void buildOptions() {
        add("List user accounts", this::listUsers);
        add("Create user account", this::createUser);
        add("Reset a user's password", this::resetPassword);
        add("Delete user account", this::deleteUser);
        add("View recent activity", this::activity);
    }

    private void listUsers() {
        Table table = new Table("Username", "Role", "Password status");
        for (User u : ctx.auth().list()) {
            table.row(u.getUsername(), u.getRole(), u.mustChangePassword() ? "Temporary (must change)" : "Set by user");
        }
        table.print();
    }

    private void createUser() {
        Views.section("Create User Account");
        String username = Input.required("Username (lowercase, 4-20 chars): ");
        Role role = Input.choose("Role: ", Role.values());
        String password = Input.password("Temporary password (8+ chars, letters and numbers): ");
        User user = ctx.auth().register(username, password, role);
        Views.ok("Account '" + user.getUsername() + "' created. They must change the password at first login.");
        ctx.log("Created user " + user.getUsername() + " (" + role + ")");
    }

    private void resetPassword() {
        listUsers();
        User user = ctx.auth().find(Input.required("Username: "));
        String password = Input.password("New temporary password: ");
        ctx.auth().resetPassword(user.getUsername(), password);
        Views.ok("Password reset. " + user.getUsername() + " must change it at next login.");
        ctx.log("Reset password of " + user.getUsername());
    }

    private void deleteUser() {
        listUsers();
        User user = ctx.auth().find(Input.required("Username: "));
        if (!Input.confirm("Delete account '" + user.getUsername() + "'?")) {
            Views.info("Cancelled.");
            return;
        }
        ctx.auth().delete(ctx.user(), user.getUsername());
        Views.ok("Account deleted.");
        ctx.log("Deleted user " + user.getUsername());
    }

    private void activity() {
        try {
            List<String> lines = ctx.activityLog().recent(25);
            Views.section("Last " + lines.size() + " actions");
            lines.forEach(line -> System.out.println("  " + line));
        } catch (IOException e) {
            throw new GymException("Could not read the activity log: " + e.getMessage());
        }
    }
}
