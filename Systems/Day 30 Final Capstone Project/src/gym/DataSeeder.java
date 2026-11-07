package gym;

import gym.model.FitnessClass;
import gym.model.Member;
import gym.model.MemberType;
import gym.model.PaymentMethod;
import gym.model.Plan;
import gym.model.Role;
import gym.model.Trainer;
import gym.service.ClassService;
import gym.service.MemberService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

/**
 * Sample data for the first run, dated relative to today so every report has something to show.
 * Everything goes through the real services, so the sample data obeys the same business rules.
 */
final class DataSeeder {
    private DataSeeder() { }

    static void seed(AppContext ctx) {
        LocalDate today = LocalDate.now();

        ctx.auth().register("admin", "admin1234", Role.ADMIN);
        ctx.auth().register("staff", "staff1234", Role.STAFF);

        MemberService members = ctx.members();
        Member juan = members.register(MemberType.REGULAR, "Juan Dela Cruz", "09171234567", "juan.delacruz@example.com", "", today.minusDays(70));
        Member maria = members.register(MemberType.STUDENT, "Maria Santos", "09182345678", "maria.santos@example.com", "National University", today.minusDays(60));
        Member pedro = members.register(MemberType.SENIOR, "Pedro Reyes", "09193456789", "", "SC-2019-004512", today.minusDays(25));
        Member ana = members.register(MemberType.REGULAR, "Ana Gonzales", "09204567890", "ana.gonzales@example.com", "", today.minusDays(100));
        Member carlo = members.register(MemberType.STUDENT, "Carlo Mendoza", "09215678901", "carlo.mendoza@example.com", "University of Santo Tomas", today.minusDays(45));
        members.register(MemberType.REGULAR, "Liza Bautista", "09226789012", "", "", today.minusDays(5));
        Member mark = members.register(MemberType.REGULAR, "Mark Villanueva", "09237890123", "mark.v@example.com", "", today.minusDays(3));
        Member grace = members.register(MemberType.REGULAR, "Grace Tan", "09248901234", "grace.tan@example.com", "", today.minusDays(27));

        // Payments in date order so receipt numbers increase with time
        pay(ctx, ana, Plan.ANNUAL, PaymentMethod.CARD, today.minusDays(100));
        pay(ctx, juan, Plan.MONTHLY, PaymentMethod.CASH, today.minusDays(70));
        pay(ctx, maria, Plan.QUARTERLY, PaymentMethod.GCASH, today.minusDays(60));
        pay(ctx, carlo, Plan.MONTHLY, PaymentMethod.CASH, today.minusDays(45));
        pay(ctx, juan, Plan.MONTHLY, PaymentMethod.GCASH, today.minusDays(40));
        pay(ctx, grace, Plan.MONTHLY, PaymentMethod.CASH, today.minusDays(27));
        pay(ctx, pedro, Plan.MONTHLY, PaymentMethod.CASH, today.minusDays(25));
        pay(ctx, juan, Plan.MONTHLY, PaymentMethod.CARD, today.minusDays(10));
        pay(ctx, mark, Plan.DAY_PASS, PaymentMethod.CASH, today.minusDays(3));

        // Past check-ins: members visit on about half of the days their plan covered
        Random random = new Random(30);   // fixed seed: the same sample data every first run
        for (int daysAgo = 20; daysAgo >= 1; daysAgo--) {
            LocalDate date = today.minusDays(daysAgo);
            for (Member m : members.list(x -> true, MemberService.BY_ID)) {
                if (isCovered(ctx, m, date) && random.nextInt(100) < 55) {
                    LocalTime time = LocalTime.of(6 + random.nextInt(15), random.nextInt(4) * 15);
                    ctx.attendance().checkIn(m.getId(), date, time);
                }
            }
        }

        Trainer ramon = ctx.trainers().add("Ramon Cruz", "09301112233", "ramon.cruz@example.com", "Strength & Conditioning");
        Trainer bea = ctx.trainers().add("Bea Lim", "09302223344", "bea.lim@example.com", "Yoga & Mobility");
        Trainer paolo = ctx.trainers().add("Paolo Garcia", "09303334455", "", "HIIT & Boxing");

        ClassService classes = ctx.classes();
        FitnessClass yoga = classes.create("Morning Yoga", bea.getId(), DayOfWeek.MONDAY, LocalTime.of(6, 30), 60, 15);
        FitnessClass hiit = classes.create("HIIT Blast", paolo.getId(), DayOfWeek.TUESDAY, LocalTime.of(18, 0), 45, 12);
        FitnessClass strength = classes.create("Strength Fundamentals", ramon.getId(), DayOfWeek.WEDNESDAY, LocalTime.of(19, 0), 60, 10);
        FitnessClass boxing = classes.create("Boxing Basics", paolo.getId(), DayOfWeek.THURSDAY, LocalTime.of(18, 30), 60, 8);
        FitnessClass mobility = classes.create("Weekend Mobility", bea.getId(), DayOfWeek.SATURDAY, LocalTime.of(8, 0), 75, 20);

        book(ctx, yoga, maria, ana, grace);
        book(ctx, hiit, juan, maria);
        book(ctx, strength, juan, ana, pedro);
        book(ctx, boxing, juan);
        book(ctx, mobility, pedro, ana, maria, grace);

        ctx.log("Sample data created");
    }

    private static void pay(AppContext ctx, Member member, Plan plan, PaymentMethod method, LocalDate date) {
        double due = ctx.memberships().quote(member, plan, date).amountDue();
        double tendered = method == PaymentMethod.CASH ? Math.ceil(due / 500) * 500 : due;
        ctx.memberships().pay(member.getId(), plan, method, tendered, "staff", date);
    }

    private static boolean isCovered(AppContext ctx, Member member, LocalDate date) {
        return ctx.memberships().historyOf(member.getId()).stream()
                .anyMatch(p -> !date.isBefore(p.coverageStart()) && !date.isAfter(p.coverageEnd()));
    }

    private static void book(AppContext ctx, FitnessClass fitnessClass, Member... attendees) {
        for (Member m : attendees) ctx.classes().book(fitnessClass.getId(), m.getId());
    }
}
