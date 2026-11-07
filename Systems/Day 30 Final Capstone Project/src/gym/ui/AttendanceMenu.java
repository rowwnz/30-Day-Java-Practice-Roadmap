package gym.ui;

import gym.AppContext;
import gym.model.CheckIn;
import gym.model.Member;
import gym.util.Fmt;
import gym.util.Input;
import gym.util.Table;

import java.time.LocalDate;
import java.util.List;

public class AttendanceMenu extends Menu {

    public AttendanceMenu(AppContext ctx) {
        super(ctx, "CHECK-IN & ATTENDANCE");
    }

    @Override
    protected void buildOptions() {
        add("Check in a member", this::checkIn);
        add("Today's check-ins", () -> showDate(LocalDate.now()));
        add("Check-ins for a date", () -> showDate(Input.date("Date", LocalDate.now())));
        add("Attendance history of a member", this::history);
    }

    private void checkIn() {
        Member member = Views.pickMember(ctx);
        CheckIn checkIn = ctx.attendance().checkIn(member.getId());
        LocalDate today = checkIn.date();
        Views.ok(member.getFullName() + " checked in at " + checkIn.time() + ".");
        Views.info("Plan valid until " + Fmt.date(member.getExpiryDate()) + " (" + member.daysRemaining(today) + " day(s) left).");
        if (member.daysRemaining(today) <= Member.EXPIRING_SOON_DAYS) {
            Views.info("Reminder: membership is expiring soon. Offer a renewal!");
        }
        ctx.log("Checked in " + member);
    }

    private void showDate(LocalDate date) {
        List<CheckIn> list = ctx.attendance().on(date);
        Views.section("Check-ins on " + Fmt.longDate(date));
        Table table = new Table("Time", "Member ID", "Name");
        for (CheckIn c : list) {
            String name = ctx.members().lookup(c.memberId()).map(Member::getFullName).orElse("(deleted)");
            table.row(c.time(), c.memberId(), name);
        }
        table.print();
        Views.info(list.size() + " visit(s).");
    }

    private void history() {
        Member member = Views.pickMember(ctx);
        List<CheckIn> visits = ctx.attendance().historyOf(member.getId());
        LocalDate today = LocalDate.now();
        Views.section("Attendance of " + member);
        Views.field("Total visits", visits.size());
        Views.field("Last 7 days", ctx.attendance().visitsSince(member.getId(), today.minusDays(6)));
        Views.field("Last 30 days", ctx.attendance().visitsSince(member.getId(), today.minusDays(29)));

        Table table = new Table("Date", "Day", "Time");
        visits.stream().limit(15).forEach(c -> table.row(Fmt.date(c.date()), Fmt.day(c.date().getDayOfWeek()), c.time()));
        table.print();
        if (visits.size() > 15) Views.info("(showing the 15 most recent visits)");
    }
}
