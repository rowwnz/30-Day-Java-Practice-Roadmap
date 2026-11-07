package gym.ui;

import gym.AppContext;
import gym.exception.GymException;
import gym.model.User;
import gym.service.AuthService;
import gym.util.Input;

/** Start screen: login with limited attempts, forced password change, then the main menu. */
public class LoginScreen {
    private final AppContext ctx;

    public LoginScreen(AppContext ctx) {
        this.ctx = ctx;
    }

    public void run() {
        while (true) {
            Views.title(Views.GYM_NAME + " - MANAGEMENT SYSTEM");
            System.out.println("  [1] Login");
            System.out.println("  [0] Exit");
            if (Input.intInRange("Choose: ", 0, 1) == 0) return;

            User user = login();
            if (user == null) continue;

            ctx.setCurrentUser(user);
            ctx.log("Logged in");
            new MainMenu(ctx).show();
            ctx.log("Logged out");
            ctx.setCurrentUser(null);
            Views.ok("Logged out.");
        }
    }

    private User login() {
        Views.section("Login");
        for (int attempt = 1; attempt <= AuthService.MAX_LOGIN_ATTEMPTS; attempt++) {
            String username = Input.required("Username: ");
            String password = Input.password("Password: ");
            try {
                User user = ctx.auth().authenticate(username, password);
                Views.ok("Welcome, " + user.getUsername() + " (" + user.getRole() + ")!");
                if (user.mustChangePassword()) setNewPassword(user, password);
                return user;
            } catch (GymException e) {
                ctx.activityLog().record("system", "Failed login for '" + username + "'");
                int left = AuthService.MAX_LOGIN_ATTEMPTS - attempt;
                Views.error(e.getMessage() + (left > 0 ? " " + left + " attempt(s) left." : ""));
            }
        }
        Views.error("Too many failed attempts. Returning to the start screen.");
        return null;
    }

    /** First login (or after an admin reset): the temporary password must be replaced. */
    private void setNewPassword(User user, String currentPassword) {
        Views.info("You are using a temporary password. Please set a new one.");
        Views.info("Rules: at least 8 characters, with both letters and numbers.");
        while (true) {
            String newPassword = Input.password("New password: ");
            String confirm = Input.password("Confirm new password: ");
            if (!newPassword.equals(confirm)) {
                Views.error("Passwords do not match.");
                continue;
            }
            try {
                ctx.auth().changePassword(user, currentPassword, newPassword);
                ctx.save();
                ctx.activityLog().record(user.getUsername(), "Set a new password");
                Views.ok("Password updated.");
                return;
            } catch (GymException e) {
                Views.error(e.getMessage());
            }
        }
    }
}
