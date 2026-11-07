package gym.model;

import java.time.LocalDate;

/** Senior citizens get a discount on presenting their Senior Citizen ID. */
public class SeniorMember extends Member {
    public static final double DISCOUNT = 0.20;

    private String seniorIdNumber;

    public SeniorMember(String id, String fullName, String phone, String email, LocalDate joinDate, String seniorIdNumber) {
        super(id, fullName, phone, email, joinDate);
        this.seniorIdNumber = seniorIdNumber;
    }

    @Override
    public MemberType getType() { return MemberType.SENIOR; }

    @Override
    public double getDiscountRate() { return DISCOUNT; }

    @Override
    public String getExtraLabel() { return "Senior Citizen ID"; }

    @Override
    public String getExtraInfo() { return seniorIdNumber; }

    @Override
    public void setExtraInfo(String value) { this.seniorIdNumber = value; }
}
