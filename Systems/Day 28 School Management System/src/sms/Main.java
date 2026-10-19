package sms;

import sms.storage.DataStore;
import sms.storage.FileStorage;
import sms.ui.MainMenu;
import sms.util.InputHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Entry point: loads saved data (or sample data on first run), shows the menu, saves on exit. */
public class Main {
    private static final Path DATA_FILE = Paths.get("sms_data.dat");

    public static void main(String[] args) {
        DataStore loaded = FileStorage.load(DATA_FILE);
        boolean firstRun = loaded == null;
        final DataStore store = firstRun ? new DataStore() : loaded;

        AppContext ctx = new AppContext(store, new InputHelper(new Scanner(System.in)));
        if (firstRun) {
            DataSeeder.seed(ctx);
            System.out.println("First run: sample teachers, courses, students and grades were loaded.");
        } else {
            System.out.println("Loaded saved data from " + DATA_FILE.toAbsolutePath());
        }

        try {
            new MainMenu(ctx, () -> save(store)).show();
        } catch (NoSuchElementException e) {
            System.out.println("\nInput closed.");
        }
        save(store);
        System.out.println("Goodbye.");
    }

    private static void save(DataStore store) {
        try {
            FileStorage.save(store, DATA_FILE);
            System.out.println("[OK] Data saved to " + DATA_FILE.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("[ERROR] Could not save data: " + e.getMessage());
        }
    }
}
