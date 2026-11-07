package gym.service;

import gym.model.CheckIn;
import gym.model.FitnessClass;
import gym.model.Member;
import gym.model.MembershipStatus;
import gym.model.Payment;
import gym.model.PaymentMethod;
import gym.model.Plan;
import gym.storage.GymData;
import gym.util.Fmt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

/** Read-only summaries for the admin: revenue, memberships, attendance and class usage. */
public class ReportService {

    public record Dashboard(int members, long active, long expiringSoon, long expired, long noPlan,
                            long checkInsToday, double revenueToday, double revenueThisMonth,
                            int trainers, int classes) { }

    public record Totals(long count, double amount) {
        Totals plus(Totals other) {
            return new Totals(count + other.count, Fmt.round2(amount + other.amount));
        }
    }

    public record ClassUsage(FitnessClass fitnessClass, String trainerName, double fillRate) { }

    private final GymData data;

    public ReportService(GymData data) {
        this.data = data;
    }

    public Dashboard dashboard() {
        LocalDate today = LocalDate.now();
        Map<MembershipStatus, Long> byStatus = new EnumMap<>(MembershipStatus.class);
        for (Member m : data.getMembers().values()) byStatus.merge(m.getStatus(today), 1L, Long::sum);

        YearMonth thisMonth = YearMonth.from(today);
        double revenueToday = 0;
        double revenueMonth = 0;
        for (Payment p : data.getPayments()) {
            if (p.date().equals(today)) revenueToday += p.amountDue();
            if (YearMonth.from(p.date()).equals(thisMonth)) revenueMonth += p.amountDue();
        }
        long checkInsToday = data.getCheckIns().stream().filter(c -> c.date().equals(today)).count();

        long expiring = byStatus.getOrDefault(MembershipStatus.EXPIRING, 0L);
        return new Dashboard(data.getMembers().size(),
                byStatus.getOrDefault(MembershipStatus.ACTIVE, 0L) + expiring,   // expiring members are still active
                expiring,
                byStatus.getOrDefault(MembershipStatus.EXPIRED, 0L),
                byStatus.getOrDefault(MembershipStatus.NO_PLAN, 0L),
                checkInsToday, Fmt.round2(revenueToday), Fmt.round2(revenueMonth),
                data.getTrainers().size(), data.getClasses().size());
    }

    public Map<YearMonth, Totals> revenueByMonth() {
        return totalsBy(p -> YearMonth.from(p.date()), TreeMap::new);
    }

    public Map<Plan, Totals> revenueByPlan() {
        return totalsBy(Payment::plan, () -> new EnumMap<>(Plan.class));
    }

    public Map<PaymentMethod, Totals> revenueByMethod() {
        return totalsBy(Payment::method, () -> new EnumMap<>(PaymentMethod.class));
    }

    public double totalRevenue() {
        return Fmt.round2(data.getPayments().stream().mapToDouble(Payment::amountDue).sum());
    }

    /** Active members whose plan ends within the given number of days, soonest first. */
    public List<Member> expiringWithin(int days) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(days);
        return data.getMembers().values().stream()
                .filter(m -> m.isActive(today) && !m.getExpiryDate().isAfter(limit))
                .sorted(MemberService.BY_EXPIRY)
                .toList();
    }

    /** Most recently lapsed first, so the newest leads to follow up are on top. */
    public List<Member> expiredMembers() {
        LocalDate today = LocalDate.now();
        return data.getMembers().values().stream()
                .filter(m -> m.getStatus(today) == MembershipStatus.EXPIRED)
                .sorted(MemberService.BY_EXPIRY.reversed())
                .toList();
    }

    /** Check-in count for each of the last N days (oldest first), including days with zero visits. */
    public Map<LocalDate, Long> dailyCheckIns(int days) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Long> counts = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) counts.put(today.minusDays(i), 0L);
        for (CheckIn c : data.getCheckIns()) counts.computeIfPresent(c.date(), (d, n) -> n + 1);
        return counts;
    }

    public List<Map.Entry<Member, Long>> topVisitors(int days, int limit) {
        LocalDate from = LocalDate.now().minusDays(days - 1);
        Map<String, Long> visits = new TreeMap<>();
        for (CheckIn c : data.getCheckIns()) {
            if (!c.date().isBefore(from)) visits.merge(c.memberId(), 1L, Long::sum);
        }
        return visits.entrySet().stream()
                .filter(e -> data.getMembers().containsKey(e.getKey()))
                .map(e -> Map.entry(data.getMembers().get(e.getKey()), e.getValue()))
                .sorted(Map.Entry.<Member, Long>comparingByValue().reversed())
                .limit(limit)
                .toList();
    }

    public List<ClassUsage> classUtilization() {
        return data.getClasses().values().stream()
                .sorted(ClassService.BY_SCHEDULE)
                .map(c -> new ClassUsage(c, trainerName(c.getTrainerId()),
                        (double) c.getBookings().size() / c.getCapacity()))
                .toList();
    }

    /** Writes a plain-text management summary and returns the file's path. */
    public Path exportSummary(Path folder) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        Dashboard d = dashboard();
        List<String> lines = new ArrayList<>();
        lines.add("IRONCORE FITNESS GYM - MANAGEMENT SUMMARY");
        lines.add("Generated: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        lines.add("");
        lines.add("MEMBERSHIP");
        lines.add(String.format("  Total members ........ %d", d.members()));
        lines.add(String.format("  Active ............... %d", d.active()));
        lines.add(String.format("  Expiring in %d days ... %d", Member.EXPIRING_SOON_DAYS, d.expiringSoon()));
        lines.add(String.format("  Expired .............. %d", d.expired()));
        lines.add(String.format("  No plan yet .......... %d", d.noPlan()));
        lines.add("");
        lines.add("REVENUE BY MONTH");
        revenueByMonth().forEach((month, t) ->
                lines.add(String.format("  %-10s %4d payment(s)  %18s", Fmt.month(month), t.count(), Fmt.peso(t.amount()))));
        lines.add(String.format("  %-10s %16s  %18s", "TOTAL", "", Fmt.peso(totalRevenue())));
        lines.add("");
        lines.add("REVENUE BY PLAN");
        revenueByPlan().forEach((plan, t) ->
                lines.add(String.format("  %-10s %4d sold        %18s", plan, t.count(), Fmt.peso(t.amount()))));
        lines.add("");
        lines.add("EXPIRING WITHIN " + Member.EXPIRING_SOON_DAYS + " DAYS");
        List<Member> expiring = expiringWithin(Member.EXPIRING_SOON_DAYS);
        if (expiring.isEmpty()) lines.add("  (none)");
        for (Member m : expiring) {
            lines.add(String.format("  %-7s %-25s %-12s expires %s", m.getId(), m.getFullName(), m.getPhone(), Fmt.date(m.getExpiryDate())));
        }
        lines.add("");
        lines.add("CLASS UTILIZATION");
        for (ClassUsage u : classUtilization()) {
            FitnessClass c = u.fitnessClass();
            lines.add(String.format("  %-24s %-10s %-11s %2d/%-2d  %s", c.getName(), Fmt.day(c.getDay()),
                    c.getTimeSlot(), c.getBookings().size(), c.getCapacity(), Fmt.percent(u.fillRate())));
        }

        Files.createDirectories(folder);
        Path file = folder.resolve("gym_summary_" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
        Files.write(file, lines, StandardCharsets.UTF_8);
        return file;
    }

    /** Groups payments by any key and totals count and amount (generic helper for the revenue reports). */
    private <K> Map<K, Totals> totalsBy(Function<Payment, K> key, Supplier<Map<K, Totals>> mapFactory) {
        Map<K, Totals> result = mapFactory.get();
        for (Payment p : data.getPayments()) {
            result.merge(key.apply(p), new Totals(1, p.amountDue()), Totals::plus);
        }
        return result;
    }

    private String trainerName(String trainerId) {
        return data.getTrainers().containsKey(trainerId) ? data.getTrainers().get(trainerId).getFullName() : "(unassigned)";
    }
}
