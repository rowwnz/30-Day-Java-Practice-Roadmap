package gym.ui;

import gym.AppContext;
import gym.exception.GymException;
import gym.model.FitnessClass;
import gym.model.Member;
import gym.service.ReportService;
import gym.util.Fmt;
import gym.util.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Admin-only reports. */
public class ReportMenu extends Menu {
    private static final int BAR_WIDTH = 30;

    public ReportMenu(AppContext ctx) {
        super(ctx, "REPORTS");
    }

    @Override
    protected void buildOptions() {
        add("Dashboard", this::dashboard);
        add("Revenue report", this::revenue);
        add("Expiring & expired memberships", this::renewals);
        add("Attendance report", this::attendance);
        add("Class utilization", this::classUsage);
        add("Export summary to a text file", this::export);
    }

    private void dashboard() {
        ReportService.Dashboard d = ctx.reports().dashboard();
        Views.section("Dashboard - " + Fmt.longDate(LocalDate.now()));
        Views.field("Total members", d.members());
        Views.field("Active", d.active() + "  (expiring within " + Member.EXPIRING_SOON_DAYS + " days: " + d.expiringSoon() + ")");
        Views.field("Expired", d.expired());
        Views.field("No plan yet", d.noPlan());
        Views.field("Check-ins today", d.checkInsToday());
        Views.field("Revenue today", Fmt.peso(d.revenueToday()));
        Views.field("Revenue this month", Fmt.peso(d.revenueThisMonth()));
        Views.field("Revenue all time", Fmt.peso(ctx.reports().totalRevenue()));
        Views.field("Trainers / classes", d.trainers() + " / " + d.classes());
    }

    private void revenue() {
        double total = ctx.reports().totalRevenue();

        Views.section("Revenue by Month");
        Table byMonth = new Table("Month", "Payments", "Revenue");
        ctx.reports().revenueByMonth().forEach((month, t) -> byMonth.row(Fmt.month(month), t.count(), Fmt.peso(t.amount())));
        byMonth.print();

        Views.section("Revenue by Plan");
        Table byPlan = new Table("Plan", "Sold", "Revenue", "Share");
        ctx.reports().revenueByPlan().forEach((plan, t) ->
                byPlan.row(plan, t.count(), Fmt.peso(t.amount()), total == 0 ? "-" : Fmt.percent(t.amount() / total)));
        byPlan.print();

        Views.section("Revenue by Payment Method");
        Table byMethod = new Table("Method", "Payments", "Revenue");
        ctx.reports().revenueByMethod().forEach((method, t) -> byMethod.row(method, t.count(), Fmt.peso(t.amount())));
        byMethod.print();

        Views.info("TOTAL REVENUE: " + Fmt.peso(total));
    }

    private void renewals() {
        Views.section("Expiring within " + Member.EXPIRING_SOON_DAYS + " days (call to renew)");
        Views.members(ctx.reports().expiringWithin(Member.EXPIRING_SOON_DAYS));
        Views.section("Expired memberships");
        Views.members(ctx.reports().expiredMembers());
    }

    private void attendance() {
        Views.section("Check-ins, last 14 days");
        Map<LocalDate, Long> daily = ctx.reports().dailyCheckIns(14);
        long max = daily.values().stream().mapToLong(Long::longValue).max().orElse(0);
        long total = 0;
        for (Map.Entry<LocalDate, Long> e : daily.entrySet()) {
            int bar = max == 0 ? 0 : (int) Math.round(e.getValue() * (double) BAR_WIDTH / max);
            System.out.printf("  %-3s %-12s | %s %d%n", Fmt.day(e.getKey().getDayOfWeek()).substring(0, 3),
                    Fmt.date(e.getKey()), "#".repeat(bar), e.getValue());
            total += e.getValue();
        }
        System.out.printf("  Average: %.1f visits per day%n", (double) total / daily.size());

        Views.section("Top 5 visitors, last 30 days");
        List<Map.Entry<Member, Long>> top = ctx.reports().topVisitors(30, 5);
        Table table = new Table("Rank", "Member ID", "Name", "Visits");
        int rank = 1;
        for (Map.Entry<Member, Long> e : top) {
            table.row(rank++, e.getKey().getId(), e.getKey().getFullName(), e.getValue());
        }
        table.print();
    }

    private void classUsage() {
        Views.section("Class Utilization");
        Table table = new Table("ID", "Class", "Schedule", "Trainer", "Booked", "Fill rate");
        for (ReportService.ClassUsage u : ctx.reports().classUtilization()) {
            FitnessClass c = u.fitnessClass();
            table.row(c.getId(), c.getName(), Fmt.day(c.getDay()) + " " + c.getTimeSlot(), u.trainerName(),
                    c.getBookings().size() + "/" + c.getCapacity(), Fmt.percent(u.fillRate()));
        }
        table.print();
    }

    private void export() {
        try {
            Path file = ctx.reports().exportSummary(Paths.get("reports"));
            Views.ok("Summary saved to " + file.toAbsolutePath());
            ctx.log("Exported summary report " + file.getFileName());
        } catch (IOException e) {
            throw new GymException("Could not write the report: " + e.getMessage());
        }
    }
}
