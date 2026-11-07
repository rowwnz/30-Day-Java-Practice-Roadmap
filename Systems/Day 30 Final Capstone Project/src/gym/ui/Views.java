package gym.ui;

import gym.AppContext;
import gym.exception.NotFoundException;
import gym.model.FitnessClass;
import gym.model.Member;
import gym.model.Payment;
import gym.model.PaymentMethod;
import gym.model.Trainer;
import gym.util.Fmt;
import gym.util.Input;
import gym.util.Table;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Shared printing helpers and "pick a record" prompts used by all menus. */
final class Views {
    static final String GYM_NAME = "IRONCORE FITNESS GYM";
    private static final int WIDTH = 60;

    private Views() { }

    static void title(String text) {
        System.out.println();
        System.out.println("  " + "=".repeat(WIDTH));
        System.out.println("  " + center(text, WIDTH));
        System.out.println("  " + "=".repeat(WIDTH));
    }

    static void section(String text) {
        System.out.println();
        System.out.println("  --- " + text + " ---");
    }

    static void ok(String message) {
        System.out.println("  [OK] " + message);
    }

    static void error(String message) {
        System.out.println("  [!] " + message);
    }

    static void info(String message) {
        System.out.println("  " + message);
    }

    static void field(String label, Object value) {
        System.out.printf("  %-18s: %s%n", label, value);
    }

    static void members(List<Member> list) {
        LocalDate today = LocalDate.now();
        Table table = new Table("ID", "Name", "Type", "Mobile", "Plan", "Expires", "Status");
        for (Member m : list) {
            table.row(m.getId(), m.getFullName(), m.getType(), m.getPhone(),
                    m.getCurrentPlan() == null ? "-" : m.getCurrentPlan(),
                    Fmt.date(m.getExpiryDate()), m.getStatus(today));
        }
        table.print();
    }

    static void payments(List<Payment> list) {
        Table table = new Table("Receipt", "Date", "Member", "Plan", "Coverage", "Method", "Amount");
        for (Payment p : list) {
            table.row(p.id(), Fmt.date(p.date()), p.memberName(), p.plan(),
                    Fmt.date(p.coverageStart()) + " - " + Fmt.date(p.coverageEnd()), p.method(), Fmt.peso(p.amountDue()));
        }
        table.print();
    }

    static void schedule(AppContext ctx, List<FitnessClass> list) {
        Table table = new Table("ID", "Class", "Day", "Time", "Trainer", "Booked");
        for (FitnessClass c : list) {
            table.row(c.getId(), c.getName(), Fmt.day(c.getDay()), c.getTimeSlot(),
                    ctx.trainers().nameOf(c.getTrainerId()), c.getBookings().size() + "/" + c.getCapacity());
        }
        table.print();
    }

    static void trainers(AppContext ctx, List<Trainer> list) {
        Table table = new Table("ID", "Name", "Specialty", "Mobile", "Email", "Classes");
        for (Trainer t : list) {
            table.row(t.getId(), t.getFullName(), t.getSpecialty(), t.getPhone(),
                    t.getEmail().isEmpty() ? "-" : t.getEmail(), ctx.trainers().classesOf(t.getId()).size());
        }
        table.print();
    }

    static void receipt(Payment p) {
        int w = 48;
        String rule = "  " + "-".repeat(w);
        System.out.println();
        System.out.println("  " + "=".repeat(w));
        System.out.println("  " + center(GYM_NAME, w));
        System.out.println("  " + center("Official Receipt", w));
        System.out.println(rule);
        receiptLine("Receipt No.", p.id());
        receiptLine("Date", Fmt.date(p.date()));
        receiptLine("Member", p.memberId() + "  " + p.memberName());
        receiptLine("Plan", p.plan() + " (" + p.plan().getDays() + (p.plan().getDays() == 1 ? " day)" : " days)"));
        receiptLine("Valid", Fmt.date(p.coverageStart()) + " to " + Fmt.date(p.coverageEnd()));
        System.out.println(rule);
        receiptAmount("Base price", p.basePrice());
        if (p.discount() > 0) receiptAmount("Less: discount", -p.discount());
        receiptAmount("AMOUNT DUE", p.amountDue());
        receiptAmount("Paid via " + p.method(), p.amountTendered());
        if (p.method() == PaymentMethod.CASH) receiptAmount("Change", p.change());
        System.out.println(rule);
        receiptLine("Processed by", p.processedBy());
        System.out.println("  " + center("Thank you and keep moving!", w));
        System.out.println("  " + "=".repeat(w));
    }

    // ---- Pickers: accept an exact ID or search by name ----

    static Member pickMember(AppContext ctx) {
        String query = Input.required("Member ID or name: ");
        Optional<Member> exact = ctx.members().lookup(query);
        if (exact.isPresent()) return exact.get();

        List<Member> matches = ctx.members().search(query);
        if (matches.isEmpty()) throw new NotFoundException("No member matches '" + query + "'.");
        if (matches.size() == 1) {
            info("Found: " + matches.get(0));
            return matches.get(0);
        }
        members(matches);
        return ctx.members().find(Input.required("Several matches. Enter the member ID: "));
    }

    static Trainer pickTrainer(AppContext ctx) {
        trainers(ctx, ctx.trainers().list());
        return ctx.trainers().find(Input.required("Trainer ID: "));
    }

    static FitnessClass pickClass(AppContext ctx) {
        schedule(ctx, ctx.classes().schedule());
        return ctx.classes().find(Input.required("Class ID: "));
    }

    private static void receiptLine(String label, String value) {
        System.out.printf("  %-13s: %s%n", label, value);
    }

    private static void receiptAmount(String label, double amount) {
        String text = amount < 0 ? "-" + Fmt.peso(-amount) : Fmt.peso(amount);
        System.out.printf("  %-28s%20s%n", label, text);
    }

    private static String center(String text, int width) {
        int left = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(left) + text;
    }
}
