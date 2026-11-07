package gym.service;

import gym.exception.NotFoundException;
import gym.model.Member;
import gym.model.MemberType;
import gym.storage.GymData;
import gym.util.Validator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/** Member CRUD and the rules that keep member records valid and unique. */
public class MemberService {
    public static final Comparator<Member> BY_ID = Comparator.comparing(Member::getId);
    public static final Comparator<Member> BY_NAME = Comparator.comparing(Member::getFullName, String.CASE_INSENSITIVE_ORDER);
    public static final Comparator<Member> BY_EXPIRY = Comparator.comparing(Member::getExpiryDate,
            Comparator.nullsLast(Comparator.naturalOrder()));

    private final GymData data;

    public MemberService(GymData data) {
        this.data = data;
    }

    public Member register(MemberType type, String name, String phone, String email, String extra) {
        return register(type, name, phone, email, extra, LocalDate.now());
    }

    public Member register(MemberType type, String name, String phone, String email, String extra, LocalDate joinDate) {
        validate(null, name, phone, email);
        Validator.require(type == MemberType.REGULAR || !extra.isBlank(),
                type + " members must provide proof of eligibility (school or senior ID).");
        Member member = Member.create(type, data.nextId("M", 4), name, phone, email, joinDate, extra.trim());
        data.getMembers().put(member.getId(), member);
        return member;
    }

    public void update(String id, String name, String phone, String email, String extra) {
        Member member = find(id);
        validate(member.getId(), name, phone, email);
        Validator.require(member.getExtraLabel() == null || !extra.isBlank(), member.getExtraLabel() + " is required.");
        member.setFullName(name);
        member.setPhone(phone);
        member.setEmail(email);
        member.setExtraInfo(extra.trim());
    }

    /** Removes the member with their check-ins and class bookings. Payments stay for revenue history. */
    public Member delete(String id) {
        Member member = find(id);
        data.getMembers().remove(member.getId());
        data.getCheckIns().removeIf(c -> c.memberId().equals(member.getId()));
        data.getClasses().values().forEach(c -> c.removeBooking(member.getId()));
        return member;
    }

    public Member find(String id) {
        return lookup(id).orElseThrow(() -> new NotFoundException("No member with ID '" + id + "'."));
    }

    public Optional<Member> lookup(String id) {
        return Optional.ofNullable(data.getMembers().get(id.trim().toUpperCase()));
    }

    /** Matches the keyword against ID, name or mobile number (case-insensitive). */
    public List<Member> search(String keyword) {
        String k = keyword.trim().toLowerCase();
        return data.getMembers().values().stream()
                .filter(m -> m.getId().toLowerCase().contains(k)
                        || m.getFullName().toLowerCase().contains(k)
                        || m.getPhone().contains(k))
                .sorted(BY_NAME)
                .toList();
    }

    public List<Member> list(Predicate<Member> filter, Comparator<Member> order) {
        return data.getMembers().values().stream().filter(filter).sorted(order).toList();
    }

    public int count() {
        return data.getMembers().size();
    }

    private void validate(String selfId, String name, String phone, String email) {
        Validator.require(Validator.isName(name), "Invalid name: use letters, spaces, periods, apostrophes or hyphens.");
        Validator.require(Validator.isMobile(phone), "Mobile number must be 11 digits starting with 09.");
        Validator.require(email.isEmpty() || Validator.isEmail(email), "Invalid email address.");
        for (Member other : data.getMembers().values()) {
            if (other.getId().equals(selfId)) continue;
            Validator.require(!other.getPhone().equals(phone), "Mobile number is already used by " + other + ".");
            Validator.require(email.isEmpty() || !other.getEmail().equalsIgnoreCase(email),
                    "Email is already used by " + other + ".");
        }
    }
}
