package gym.ui;

import gym.AppContext;
import gym.model.FitnessClass;
import gym.model.Member;
import gym.model.Trainer;
import gym.service.ClassService;
import gym.util.Fmt;
import gym.util.Input;
import gym.util.Table;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class ClassMenu extends Menu {

    public ClassMenu(AppContext ctx) {
        super(ctx, "CLASSES & TRAINERS");
    }

    @Override
    protected void buildOptions() {
        add("Weekly class schedule", () -> Views.schedule(ctx, ctx.classes().schedule()));
        add("View class roster", this::roster);
        add("Book a member into a class", this::book);
        add("Cancel a booking", this::cancel);
        if (ctx.isAdmin()) {
            add("Create class", this::create);
            add("Reschedule / change trainer", this::reschedule);
            add("Change class capacity", this::capacity);
            add("Delete class", this::delete);
        }
        addSubmenu("Trainers", () -> new TrainerMenu(ctx));
    }

    private void roster() {
        FitnessClass c = Views.pickClass(ctx);
        Views.section(c.getName() + " - " + Fmt.day(c.getDay()) + " " + c.getTimeSlot()
                + " with " + ctx.trainers().nameOf(c.getTrainerId()));
        LocalDate today = LocalDate.now();
        Table table = new Table("#", "Member ID", "Name", "Mobile", "Status");
        int n = 1;
        for (String id : c.getBookings()) {
            Member m = ctx.members().find(id);
            table.row(n++, m.getId(), m.getFullName(), m.getPhone(), m.getStatus(today));
        }
        table.print();
        Views.info(c.getBookings().size() + " booked, " + c.getAvailableSlots() + " slot(s) left.");
    }

    private void book() {
        FitnessClass c = Views.pickClass(ctx);
        Member m = Views.pickMember(ctx);
        ctx.classes().book(c.getId(), m.getId());
        Views.ok(m.getFullName() + " booked in " + c.getName() + " (" + Fmt.day(c.getDay()) + " " + c.getTimeSlot() + "). "
                + c.getAvailableSlots() + " slot(s) left.");
        ctx.log("Booked " + m + " in " + c.getId());
    }

    private void cancel() {
        FitnessClass c = Views.pickClass(ctx);
        Member m = Views.pickMember(ctx);
        ctx.classes().cancel(c.getId(), m.getId());
        Views.ok("Booking cancelled.");
        ctx.log("Cancelled booking of " + m + " in " + c.getId());
    }

    private void create() {
        Views.section("Create Class");
        String name = Input.required("Class name: ");
        Trainer trainer = Views.pickTrainer(ctx);
        DayOfWeek day = Input.choose("Day: ", DayOfWeek.values());
        LocalTime start = Input.time("Start time");
        int minutes = Input.intInRange("Duration in minutes (15-180): ", 15, 180);
        int capacity = Input.intInRange("Capacity (1-" + ClassService.MAX_CAPACITY + "): ", 1, ClassService.MAX_CAPACITY);

        FitnessClass c = ctx.classes().create(name, trainer.getId(), day, start, minutes, capacity);
        Views.ok("Created " + c.getId() + " " + c.getName() + " every " + Fmt.day(day) + " " + c.getTimeSlot() + ".");
        ctx.log("Created class " + c.getId() + " " + c.getName());
    }

    private void reschedule() {
        FitnessClass c = Views.pickClass(ctx);
        Views.info("Choose the new trainer (can be the same one).");
        Trainer trainer = Views.pickTrainer(ctx);
        DayOfWeek day = Input.choose("Day: ", DayOfWeek.values());
        LocalTime start = Input.time("Start time");
        int minutes = Input.intInRange("Duration in minutes (15-180): ", 15, 180);

        ctx.classes().reschedule(c.getId(), trainer.getId(), day, start, minutes);
        Views.ok(c.getName() + " is now every " + Fmt.day(day) + " " + c.getTimeSlot() + " with " + trainer.getFullName() + ".");
        ctx.log("Rescheduled class " + c.getId());
    }

    private void capacity() {
        FitnessClass c = Views.pickClass(ctx);
        int capacity = Input.intInRange("New capacity (currently " + c.getCapacity() + "): ", 1, ClassService.MAX_CAPACITY);
        ctx.classes().changeCapacity(c.getId(), capacity);
        Views.ok("Capacity of " + c.getName() + " set to " + capacity + ".");
        ctx.log("Changed capacity of " + c.getId() + " to " + capacity);
    }

    private void delete() {
        FitnessClass c = Views.pickClass(ctx);
        if (!Input.confirm("Delete " + c.getName() + " and its " + c.getBookings().size() + " booking(s)?")) {
            Views.info("Cancelled.");
            return;
        }
        ctx.classes().delete(c.getId());
        Views.ok("Class deleted.");
        ctx.log("Deleted class " + c.getId() + " " + c.getName());
    }
}
