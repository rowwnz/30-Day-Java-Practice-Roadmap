package gym.service;

import gym.model.CheckIn;
import gym.model.Member;
import gym.storage.GymData;
import gym.util.Fmt;
import gym.util.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

/** Front-desk check-ins: one per member per day, active members only. */
public class AttendanceService {
    private final GymData data;
    private final MemberService members;

    public AttendanceService(GymData data, MemberService members) {
        this.data = data;
        this.members = members;
    }

    public CheckIn checkIn(String memberId) {
        LocalDateTime now = LocalDateTime.now();
        return checkIn(memberId, now.toLocalDate(), now.toLocalTime().truncatedTo(ChronoUnit.MINUTES));
    }

    public CheckIn checkIn(String memberId, LocalDate date, LocalTime time) {
        Member member = members.find(memberId);
        Validator.require(member.isActive(date), member.getFullName() + " cannot check in: membership is "
                + member.getStatus(date) + ". Please renew first.");
        Validator.require(!hasCheckedIn(member.getId(), date),
                member.getFullName() + " already checked in on " + Fmt.date(date) + ".");
        CheckIn checkIn = new CheckIn(member.getId(), date, time);
        data.getCheckIns().add(checkIn);
        return checkIn;
    }

    public boolean hasCheckedIn(String memberId, LocalDate date) {
        return data.getCheckIns().stream().anyMatch(c -> c.memberId().equals(memberId) && c.date().equals(date));
    }

    public List<CheckIn> on(LocalDate date) {
        return data.getCheckIns().stream()
                .filter(c -> c.date().equals(date))
                .sorted(Comparator.comparing(CheckIn::time))
                .toList();
    }

    /** Newest first. */
    public List<CheckIn> historyOf(String memberId) {
        return data.getCheckIns().stream()
                .filter(c -> c.memberId().equals(memberId))
                .sorted(Comparator.comparing(CheckIn::date).thenComparing(CheckIn::time).reversed())
                .toList();
    }

    public long visitsSince(String memberId, LocalDate from) {
        return data.getCheckIns().stream()
                .filter(c -> c.memberId().equals(memberId) && !c.date().isBefore(from))
                .count();
    }
}
