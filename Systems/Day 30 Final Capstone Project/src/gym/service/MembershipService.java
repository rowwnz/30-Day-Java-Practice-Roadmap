package gym.service;

import gym.exception.NotFoundException;
import gym.model.Member;
import gym.model.Payment;
import gym.model.PaymentMethod;
import gym.model.Plan;
import gym.storage.GymData;
import gym.util.Fmt;
import gym.util.Validator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/** Pricing, renewals and payments. */
public class MembershipService {
    /** A membership can be prepaid at most this many days ahead. */
    public static final int MAX_ADVANCE_DAYS = 365;

    /** Price breakdown and coverage period for buying a plan on a given date. */
    public record Quote(Plan plan, double basePrice, double discount, double amountDue,
                        LocalDate coverageStart, LocalDate coverageEnd) { }

    private final GymData data;
    private final MemberService members;

    public MembershipService(GymData data, MemberService members) {
        this.data = data;
        this.members = members;
    }

    public Quote quote(Member member, Plan plan, LocalDate date) {
        double base = plan.getPrice();
        double discount = Fmt.round2(base * member.getDiscountRate());
        // Renewing early stacks on top of the current expiry; a lapsed membership restarts on the payment date
        LocalDate start = member.isActive(date) ? member.getExpiryDate().plusDays(1) : date;
        Validator.require(!start.isAfter(date.plusDays(MAX_ADVANCE_DAYS)), member.getFullName()
                + " is already paid through " + Fmt.date(member.getExpiryDate())
                + ". Memberships can only be prepaid up to one year ahead.");
        return new Quote(plan, base, discount, Fmt.round2(base - discount), start, start.plusDays(plan.getDays() - 1));
    }

    public Payment pay(String memberId, Plan plan, PaymentMethod method, double tendered, String processedBy) {
        return pay(memberId, plan, method, tendered, processedBy, LocalDate.now());
    }

    public Payment pay(String memberId, Plan plan, PaymentMethod method, double tendered,
                       String processedBy, LocalDate date) {
        Member member = members.find(memberId);
        Quote q = quote(member, plan, date);
        double paid;
        if (method == PaymentMethod.CASH) {
            Validator.require(tendered >= q.amountDue(), "Insufficient cash. Amount due is " + Fmt.peso(q.amountDue()) + ".");
            paid = Fmt.round2(tendered);
        } else {
            paid = q.amountDue();   // card and e-wallet payments are always exact
        }

        Payment payment = new Payment(data.nextId("PAY", 5), member.getId(), member.getFullName(), plan,
                q.basePrice(), q.discount(), q.amountDue(), method, paid,
                date, q.coverageStart(), q.coverageEnd(), processedBy);
        data.getPayments().add(payment);
        member.renew(plan, q.coverageEnd());
        return payment;
    }

    public Payment find(String paymentId) {
        String id = paymentId.trim().toUpperCase();
        return data.getPayments().stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No payment with receipt number '" + paymentId + "'."));
    }

    public List<Payment> historyOf(String memberId) {
        return data.getPayments().stream()
                .filter(p -> p.memberId().equals(memberId))
                .sorted(Comparator.comparing(Payment::date).thenComparing(Payment::id))
                .toList();
    }

    public List<Payment> on(LocalDate date) {
        return data.getPayments().stream().filter(p -> p.date().equals(date)).toList();
    }
}
