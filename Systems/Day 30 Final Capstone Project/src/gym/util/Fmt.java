package gym.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/** Formatting helpers so money and dates look the same on every screen. */
public final class Fmt {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    private Fmt() { }

    public static String peso(double amount) {
        return String.format(Locale.US, "PHP %,.2f", amount);
    }

    public static String date(LocalDate date) {
        return date == null ? "-" : date.format(DATE);
    }

    public static String longDate(LocalDate date) {
        return date.format(LONG_DATE);
    }

    public static String month(YearMonth month) {
        return month.format(MONTH);
    }

    public static String day(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public static String percent(double rate) {
        return String.format(Locale.US, "%.0f%%", rate * 100);
    }

    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
