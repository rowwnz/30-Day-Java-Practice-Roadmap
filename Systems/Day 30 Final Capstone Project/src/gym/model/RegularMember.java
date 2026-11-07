package gym.model;

import java.time.LocalDate;

/** Pays the full plan price. */
public class RegularMember extends Member {
    public RegularMember(String id, String fullName, String phone, String email, LocalDate joinDate) {
        super(id, fullName, phone, email, joinDate);
    }

    @Override
    public MemberType getType() { return MemberType.REGULAR; }

    @Override
    public double getDiscountRate() { return 0.0; }
}
