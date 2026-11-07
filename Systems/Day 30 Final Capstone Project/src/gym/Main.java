package gym;

import gym.storage.FileStorage;
import gym.storage.GymData;
import gym.ui.LoginScreen;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

/**
 * Day 30 - Final Capstone Project: IronCore Fitness Gym Management System.
 * Entry point: load data (or create sample data on first run), run the login screen, save on exit.
 */
public class Main {
    public static void main(String[] args) {
        FileStorage storage = new FileStorage(Paths.get("data"));
        AppContext ctx;
        try {
            ctx = open(storage);
        } catch (IOException e) {
            System.out.println("Could not load data from " + storage.getDirectory().toAbsolutePath() + ": " + e.getMessage());
            return;
        }

        try {
            new LoginScreen(ctx).run();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("  Input closed.");
        } finally {
            ctx.save();
        }
        System.out.println("  Data saved to " + storage.getDirectory().toAbsolutePath() + ". Goodbye!");
    }

    private static AppContext open(FileStorage storage) throws IOException {
        if (storage.exists()) {
            return new AppContext(storage, storage.load());
        }
        System.out.println("  First run: creating sample data in " + storage.getDirectory().toAbsolutePath());
        AppContext ctx = new AppContext(storage, new GymData());
        DataSeeder.seed(ctx);
        ctx.save();
        System.out.println("  Default logins -> admin / admin1234   and   staff / staff1234");
        System.out.println("  (you will be asked to choose a new password the first time you log in)");
        return ctx;
    }
}
