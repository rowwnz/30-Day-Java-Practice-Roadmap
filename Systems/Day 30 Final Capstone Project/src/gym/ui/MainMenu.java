package gym.ui;

import gym.AppContext;
import gym.service.ReportService;
import gym.util.Fmt;
import gym.util.Input;

import java.time.LocalDate;

/** Top-level menu. Reports and user accounts are only shown to administrators. */
public class MainMenu extends Menu {

    public MainMenu(AppContext ctx) {
        super(ctx, "MAIN MENU");
    }

    @Override
    protected void buildOptions() {
        addSubmenu("Members", () -> new MemberMenu(ctx));
        addSubmenu("Memberships & Payments", () -> new MembershipMenu(ctx));
        addSubmenu("Check-in & Attendance", () -> new AttendanceMenu(ctx));
        addSubmenu("Classes & Trainers", () -> new ClassMenu(ctx));
        if (ctx.isAdmin()) {
            addSubmenu("Reports", () -> new ReportMenu(ctx));
            addSubmenu("User Accounts & Activity Log", () -> new AdminMenu(ctx));
        }
        add("Change my password", this::changePassword);
    }

    @Override
    protected void printHeader() {
        ReportService.Dashboard d = ctx.reports().dashboard();
        System.out.printf("  %s (%s)  |  %s%n", ctx.user().getUsername(), ctx.user().getRole(), Fmt.longDate(LocalDate.now()));
        System.out.printf("  Active members: %d  |  Expiring soon: %d  |  Check-ins today: %d%n%n",
                d.active(), d.expiringSoon(), d.checkInsToday());
    }

    @Override
    protected String exitLabel() {
        return "Logout";
    }

    private void changePassword() {
        Views.section("Change Password");
        String current = Input.password("Current password: ");
        String newPassword = Input.password("New password (8+ chars, letters and numbers): ");
        String confirm = Input.password("Confirm new password: ");
        if (!newPassword.equals(confirm)) {
            Views.error("Passwords do not match. Nothing was changed.");
            return;
        }
        ctx.auth().changePassword(ctx.user(), current, newPassword);
        Views.ok("Password changed.");
        ctx.log("Changed own password");
    }
}
