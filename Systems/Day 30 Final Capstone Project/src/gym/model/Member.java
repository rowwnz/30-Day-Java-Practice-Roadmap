package gym.model;

import gym.util.Csv;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A gym member. Each subclass decides its own discount and extra ID field (polymorphism).
 */
public abstract class Member extends Person {
    public static final int EXPIRING_SOON_DAYS = 7;

    private final LocalDate joinDate;
    private Plan currentPlan;       // null until the first payment
    private LocalDate expiryDate;   // null until the first payment

    protected Member(String id, String fullName, String phone, String email, LocalDate joinDate) {
        super(id, fullName, phone, email);
        this.joinDate = joinDate;
    }

    public abstract MemberType getType();

    /** Fraction taken off the plan price, e.g. 0.15 for 15%. */
    public abstract double getDiscountRate();

    /** Label of the type-specific field (school, senior ID), or null if this type has none. */
    public String getExtraLabel() { return null; }

    public String getExtraInfo() { return ""; }

    public void setExtraInfo(String value) { }

    @Override
    public String getRoleLabel() {
        return getType().getLabel() + " Member";
    }

    public LocalDate getJoinDate() { return joinDate; }
    public Plan getCurrentPlan() { return currentPlan; }
    public LocalDate getExpiryDate() { return expiryDate; }

    public void renew(Plan plan, LocalDate newExpiry) {
        this.currentPlan = plan;
        this.expiryDate = newExpiry;
    }

    public boolean isActive(LocalDate today) {
        return expiryDate != null && !today.isAfter(expiryDate);
    }

    /** Days of access left, counting today; 0 when not active. */
    public long daysRemaining(LocalDate today) {
        if (!isActive(today)) return 0;
        return ChronoUnit.DAYS.between(today, expiryDate) + 1;
    }

    public MembershipStatus getStatus(LocalDate today) {
        if (expiryDate == null) return MembershipStatus.NO_PLAN;
        if (today.isAfter(expiryDate)) return MembershipStatus.EXPIRED;
        if (daysRemaining(today) <= EXPIRING_SOON_DAYS) return MembershipStatus.EXPIRING;
        return MembershipStatus.ACTIVE;
    }

    @Override
    public String toCsv() {
        return Csv.join(getType(), getId(), getFullName(), getPhone(), getEmail(), joinDate,
                currentPlan, expiryDate, getExtraInfo());
    }

    public static Member fromCsv(String[] f) {
        Member member = create(MemberType.valueOf(f[0]), f[1], f[2], f[3], f[4], LocalDate.parse(f[5]), f[8]);
        if (!f[6].isEmpty()) member.currentPlan = Plan.valueOf(f[6]);
        if (!f[7].isEmpty()) member.expiryDate = LocalDate.parse(f[7]);
        return member;
    }

    /** Factory method: builds the right subclass for the given type. */
    public static Member create(MemberType type, String id, String name, String phone, String email,
                                LocalDate joinDate, String extra) {
        return switch (type) {
            case REGULAR -> new RegularMember(id, name, phone, email, joinDate);
            case STUDENT -> new StudentMember(id, name, phone, email, joinDate, extra);
            case SENIOR -> new SeniorMember(id, name, phone, email, joinDate, extra);
        };
    }
}
