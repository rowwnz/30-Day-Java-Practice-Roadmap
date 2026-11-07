package gym.ui;

import gym.AppContext;
import gym.model.CheckIn;
import gym.model.FitnessClass;
import gym.model.Member;
import gym.model.MemberType;
import gym.model.MembershipStatus;
import gym.service.MemberService;
import gym.util.Fmt;
import gym.util.Input;
import gym.util.Validator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class MemberMenu extends Menu {

    public MemberMenu(AppContext ctx) {
        super(ctx, "MEMBERS");
    }

    @Override
    protected void buildOptions() {
        add("Register new member", this::register);
        add("List members", this::list);
        add("Search members", this::search);
        add("View member profile", this::profile);
        add("Update member details", this::update);
        if (ctx.isAdmin()) add("Delete member", this::delete);
    }

    private void register() {
        Views.section("Register New Member");
        MemberType type = Input.choose("Member type: ", MemberType.values());
        String name = Input.name("Full name: ");
        String phone = Input.mobile("Mobile number: ");
        String email = Input.email("Email (optional): ");
        String extra = switch (type) {
            case STUDENT -> Input.required("School / university: ");
            case SENIOR -> Input.required("Senior Citizen ID No.: ");
            case REGULAR -> "";
        };

        Member member = ctx.members().register(type, name, phone, email, extra);
        Views.ok("Registered " + member.getFullName() + " as " + member.getId()
                + " (" + Fmt.percent(member.getDiscountRate()) + " discount on all plans).");
        Views.info("Next step: Memberships & Payments > New membership / renewal.");
        ctx.log("Registered member " + member);
    }

    private void list() {
        Views.section("List Members");
        LocalDate today = LocalDate.now();
        System.out.println("  Show: 1. All  2. Active  3. Expiring soon  4. Expired  5. No plan yet");
        Predicate<Member> filter = switch (Input.intInRange("Filter: ", 1, 5)) {
            case 2 -> m -> m.isActive(today);
            case 3 -> m -> m.getStatus(today) == MembershipStatus.EXPIRING;
            case 4 -> m -> m.getStatus(today) == MembershipStatus.EXPIRED;
            case 5 -> m -> m.getStatus(today) == MembershipStatus.NO_PLAN;
            default -> m -> true;
        };
        System.out.println("  Sort by: 1. ID  2. Name  3. Expiry date");
        Comparator<Member> order = switch (Input.intInRange("Sort: ", 1, 3)) {
            case 2 -> MemberService.BY_NAME;
            case 3 -> MemberService.BY_EXPIRY;
            default -> MemberService.BY_ID;
        };

        List<Member> result = ctx.members().list(filter, order);
        Views.members(result);
        Views.info(result.size() + " of " + ctx.members().count() + " member(s) shown.");
    }

    private void search() {
        String keyword = Input.required("Search by ID, name or mobile: ");
        Views.members(ctx.members().search(keyword));
    }

    private void profile() {
        Member m = Views.pickMember(ctx);
        LocalDate today = LocalDate.now();

        Views.section("Member Profile");
        Views.field("ID", m.getId());
        Views.field("Name", m.getFullName());
        Views.field("Type", m.getRoleLabel() + " (" + Fmt.percent(m.getDiscountRate()) + " discount)");
        if (m.getExtraLabel() != null) Views.field(m.getExtraLabel(), m.getExtraInfo());
        Views.field("Mobile", m.getPhone());
        Views.field("Email", m.getEmail().isEmpty() ? "-" : m.getEmail());
        Views.field("Member since", Fmt.date(m.getJoinDate()));
        Views.field("Current plan", m.getCurrentPlan() == null ? "-" : m.getCurrentPlan());
        Views.field("Valid until", Fmt.date(m.getExpiryDate())
                + (m.isActive(today) ? " (" + m.daysRemaining(today) + " day(s) left)" : ""));
        Views.field("Status", m.getStatus(today));
        Views.field("Visits (30 days)", ctx.attendance().visitsSince(m.getId(), today.minusDays(29)));

        List<CheckIn> visits = ctx.attendance().historyOf(m.getId());
        Views.field("Last visit", visits.isEmpty() ? "-" : Fmt.date(visits.get(0).date()) + " " + visits.get(0).time());

        List<FitnessClass> classes = ctx.classes().classesOf(m.getId());
        Views.field("Booked classes", classes.isEmpty() ? "-" : "");
        for (FitnessClass c : classes) {
            Views.info("    " + c.getName() + " - " + Fmt.day(c.getDay()) + " " + c.getTimeSlot());
        }

        Views.section("Payment History");
        Views.payments(ctx.memberships().historyOf(m.getId()));
    }

    private void update() {
        Member m = Views.pickMember(ctx);
        Views.section("Update " + m);
        Views.info("Press Enter to keep the current value.");
        String name = Input.optional("Full name", m.getFullName()).replaceAll("\\s+", " ");
        String phone = Validator.normalizeMobile(Input.optional("Mobile", m.getPhone()));
        String email = Input.optional("Email (type - to clear)", m.getEmail());
        if (email.equals("-")) email = "";
        String extra = m.getExtraLabel() == null ? "" : Input.optional(m.getExtraLabel(), m.getExtraInfo());

        ctx.members().update(m.getId(), name, phone, email, extra);
        Views.ok("Member " + m.getId() + " updated.");
        ctx.log("Updated member " + m);
    }

    private void delete() {
        Member m = Views.pickMember(ctx);
        Views.info("This removes " + m + " with their check-ins and class bookings.");
        Views.info("Payment records are kept for revenue history.");
        if (!Input.confirm("Delete this member?")) {
            Views.info("Cancelled.");
            return;
        }
        ctx.members().delete(m.getId());
        Views.ok("Member deleted.");
        ctx.log("Deleted member " + m);
    }
}
