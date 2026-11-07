package gym.ui;

import gym.AppContext;
import gym.model.Trainer;
import gym.util.Input;
import gym.util.Validator;

public class TrainerMenu extends Menu {

    public TrainerMenu(AppContext ctx) {
        super(ctx, "TRAINERS");
    }

    @Override
    protected void buildOptions() {
        add("List trainers", () -> Views.trainers(ctx, ctx.trainers().list()));
        add("Trainer's weekly schedule", this::schedule);
        if (ctx.isAdmin()) {
            add("Add trainer", this::addTrainer);
            add("Update trainer", this::update);
            add("Delete trainer", this::delete);
        }
    }

    private void schedule() {
        Trainer t = Views.pickTrainer(ctx);
        Views.section("Schedule of " + t.getFullName());
        Views.schedule(ctx, ctx.trainers().classesOf(t.getId()));
    }

    private void addTrainer() {
        Views.section("Add Trainer");
        String name = Input.name("Full name: ");
        String phone = Input.mobile("Mobile number: ");
        String email = Input.email("Email (optional): ");
        String specialty = Input.required("Specialty (e.g. Yoga, Strength): ");
        Trainer t = ctx.trainers().add(name, phone, email, specialty);
        Views.ok("Added trainer " + t + ".");
        ctx.log("Added trainer " + t);
    }

    private void update() {
        Trainer t = Views.pickTrainer(ctx);
        Views.info("Press Enter to keep the current value.");
        String name = Input.optional("Full name", t.getFullName()).replaceAll("\\s+", " ");
        String phone = Validator.normalizeMobile(Input.optional("Mobile", t.getPhone()));
        String email = Input.optional("Email (type - to clear)", t.getEmail());
        if (email.equals("-")) email = "";
        String specialty = Input.optional("Specialty", t.getSpecialty());
        ctx.trainers().update(t.getId(), name, phone, email, specialty);
        Views.ok("Trainer " + t.getId() + " updated.");
        ctx.log("Updated trainer " + t);
    }

    private void delete() {
        Trainer t = Views.pickTrainer(ctx);
        if (!Input.confirm("Delete trainer " + t + "?")) {
            Views.info("Cancelled.");
            return;
        }
        ctx.trainers().delete(t.getId());
        Views.ok("Trainer deleted.");
        ctx.log("Deleted trainer " + t);
    }
}
