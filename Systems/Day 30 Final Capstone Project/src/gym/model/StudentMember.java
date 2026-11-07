package gym.model;

import java.time.LocalDate;

/** Students get a discount on presenting a school ID. */
public class StudentMember extends Member {
    public static final double DISCOUNT = 0.15;

    private String school;

    public StudentMember(String id, String fullName, String phone, String email, LocalDate joinDate, String school) {
        super(id, fullName, phone, email, joinDate);
        this.school = school;
    }

    @Override
    public MemberType getType() { return MemberType.STUDENT; }

    @Override
    public double getDiscountRate() { return DISCOUNT; }

    @Override
    public String getExtraLabel() { return "School"; }

    @Override
    public String getExtraInfo() { return school; }

    @Override
    public void setExtraInfo(String value) { this.school = value; }
}
