package gym;

import gym.model.User;
import gym.service.AttendanceService;
import gym.service.AuthService;
import gym.service.ClassService;
import gym.service.MemberService;
import gym.service.MembershipService;
import gym.service.ReportService;
import gym.service.TrainerService;
import gym.storage.ActivityLog;
import gym.storage.FileStorage;
import gym.storage.GymData;

import java.io.IOException;

/** Creates every service once and hands the same instances to the menus (manual dependency injection). */
public class AppContext {
    private final FileStorage storage;
    private final GymData data;
    private final ActivityLog activityLog;
    private final AuthService auth;
    private final MemberService members;
    private final TrainerService trainers;
    private final MembershipService memberships;
    private final AttendanceService attendance;
    private final ClassService classes;
    private final ReportService reports;
    private User currentUser;

    public AppContext(FileStorage storage, GymData data) {
        this.storage = storage;
        this.data = data;
        this.activityLog = new ActivityLog(storage.getDirectory().resolve("activity.log"));
        this.auth = new AuthService(data);
        this.members = new MemberService(data);
        this.trainers = new TrainerService(data);
        this.memberships = new MembershipService(data, members);
        this.attendance = new AttendanceService(data, members);
        this.classes = new ClassService(data, members, trainers);
        this.reports = new ReportService(data);
    }

    public AuthService auth() { return auth; }
    public MemberService members() { return members; }
    public TrainerService trainers() { return trainers; }
    public MembershipService memberships() { return memberships; }
    public AttendanceService attendance() { return attendance; }
    public ClassService classes() { return classes; }
    public ReportService reports() { return reports; }
    public ActivityLog activityLog() { return activityLog; }

    public User user() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public boolean isAdmin() { return currentUser != null && currentUser.isAdmin(); }

    /** Records an action in the activity log under the logged-in user's name. */
    public void log(String action) {
        activityLog.record(currentUser == null ? "system" : currentUser.getUsername(), action);
    }

    public void save() {
        try {
            storage.save(data);
        } catch (IOException e) {
            System.out.println("  ERROR: could not save data: " + e.getMessage());
        }
    }
}
