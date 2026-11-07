package gym.ui;

import gym.AppContext;
import gym.model.Member;
import gym.model.Payment;
import gym.model.PaymentMethod;
import gym.model.Plan;
import gym.model.SeniorMember;
import gym.model.StudentMember;
import gym.service.MembershipService;
import gym.util.Fmt;
import gym.util.Input;
import gym.util.Table;

import java.time.LocalDate;
import java.util.List;

public class MembershipMenu extends Menu {

    public MembershipMenu(AppContext ctx) {
        super(ctx, "MEMBERSHIPS & PAYMENTS");
    }

    @Override
    protected void buildOptions() {
        add("View plans & prices", this::showPlans);
        add("New membership / renewal (payment)", this::processPayment);
        add("Payment history of a member", this::history);
        add("Payments for a date", this::paymentsOnDate);
        add("Reprint a receipt", this::reprint);
    }

    private void showPlans() {
        Views.section("Membership Plans");
        Table table = new Table("#", "Plan", "Days", "Regular",
                "Student (-" + Fmt.percent(StudentMember.DISCOUNT) + ")",
                "Senior (-" + Fmt.percent(SeniorMember.DISCOUNT) + ")");
        Plan[] plans = Plan.values();
        for (int i = 0; i < plans.length; i++) {
            Plan p = plans[i];
            table.row(i + 1, p, p.getDays(), Fmt.peso(p.getPrice()),
                    Fmt.peso(Fmt.round2(p.getPrice() * (1 - StudentMember.DISCOUNT))),
                    Fmt.peso(Fmt.round2(p.getPrice() * (1 - SeniorMember.DISCOUNT))));
        }
        table.print();
        Views.info("Renewing early adds the new days after the current expiry date.");
    }

    private void processPayment() {
        Member member = Views.pickMember(ctx);
        LocalDate today = LocalDate.now();
        Views.info("Member  : " + member + " (" + member.getRoleLabel() + ")");
        Views.info("Status  : " + member.getStatus(today)
                + (member.getExpiryDate() == null ? "" : ", valid until " + Fmt.date(member.getExpiryDate())));

        showPlans();
        Plan plan = Plan.values()[Input.intInRange("Choose plan #: ", 1, Plan.values().length) - 1];
        MembershipService.Quote q = ctx.memberships().quote(member, plan, today);

        Views.section("Payment Summary");
        Views.field("Plan", plan + " (" + plan.getDays() + " day(s))");
        Views.field("Coverage", Fmt.date(q.coverageStart()) + " to " + Fmt.date(q.coverageEnd()));
        Views.field("Base price", Fmt.peso(q.basePrice()));
        if (q.discount() > 0) {
            Views.field("Discount", "-" + Fmt.peso(q.discount()) + " (" + member.getType() + ")");
        }
        Views.field("AMOUNT DUE", Fmt.peso(q.amountDue()));

        PaymentMethod method = Input.choose("Payment method: ", PaymentMethod.values());
        double tendered = q.amountDue();
        if (method == PaymentMethod.CASH) {
            tendered = Input.money("Cash received: ", q.amountDue(), 1_000_000);
        }
        if (!Input.confirm("Confirm payment of " + Fmt.peso(q.amountDue()) + "?")) {
            Views.info("Payment cancelled. Nothing was recorded.");
            return;
        }

        Payment payment = ctx.memberships().pay(member.getId(), plan, method, tendered, ctx.user().getUsername());
        Views.receipt(payment);
        ctx.log("Payment " + payment.id() + ": " + member + ", " + plan + ", " + Fmt.peso(payment.amountDue()));
    }

    private void history() {
        Member member = Views.pickMember(ctx);
        List<Payment> list = ctx.memberships().historyOf(member.getId());
        Views.section("Payments of " + member);
        Views.payments(list);
        double total = list.stream().mapToDouble(Payment::amountDue).sum();
        Views.info("Total paid: " + Fmt.peso(total));
    }

    private void paymentsOnDate() {
        LocalDate date = Input.date("Date", LocalDate.now());
        List<Payment> list = ctx.memberships().on(date);
        Views.section("Payments on " + Fmt.date(date));
        Views.payments(list);
        Views.info(list.size() + " payment(s), total " + Fmt.peso(list.stream().mapToDouble(Payment::amountDue).sum()));
    }

    private void reprint() {
        Payment payment = ctx.memberships().find(Input.required("Receipt number (e.g. PAY-00001): "));
        Views.receipt(payment);
    }
}
