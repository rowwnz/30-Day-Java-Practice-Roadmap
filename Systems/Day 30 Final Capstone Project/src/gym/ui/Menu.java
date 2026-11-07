package gym.ui;

import gym.AppContext;
import gym.exception.GymException;
import gym.util.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base class for every menu screen (Template Method pattern).
 * Subclasses register options in buildOptions(); show() runs the loop, catches business errors and auto-saves.
 */
public abstract class Menu {
    private record Option(String label, Runnable action, boolean pauseAfter) { }

    protected final AppContext ctx;
    private final String title;
    private final List<Option> options = new ArrayList<>();

    protected Menu(AppContext ctx, String title) {
        this.ctx = ctx;
        this.title = title;
    }

    /** Called once, the first time the menu is shown. */
    protected abstract void buildOptions();

    protected final void add(String label, Runnable action) {
        options.add(new Option(label, action, true));
    }

    protected final void addSubmenu(String label, Supplier<Menu> submenu) {
        options.add(new Option(label, () -> submenu.get().show(), false));
    }

    /** Printed under the title each time the menu is drawn. */
    protected void printHeader() { }

    protected String exitLabel() {
        return "Back";
    }

    public void show() {
        if (options.isEmpty()) buildOptions();
        while (true) {
            Views.title(title);
            printHeader();
            for (int i = 0; i < options.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, options.get(i).label());
            }
            System.out.printf("  [0] %s%n", exitLabel());

            int choice = Input.intInRange("Choose: ", 0, options.size());
            if (choice == 0) return;

            Option option = options.get(choice - 1);
            try {
                option.action().run();
                ctx.save();   // auto-save after every action so nothing is lost if the program is closed
            } catch (GymException e) {
                Views.error(e.getMessage());
            }
            if (option.pauseAfter()) Input.pause();
        }
    }
}
