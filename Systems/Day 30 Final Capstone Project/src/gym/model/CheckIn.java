package gym.model;

import gym.util.Csv;

import java.time.LocalDate;
import java.time.LocalTime;

/** One gym visit. */
public record CheckIn(String memberId, LocalDate date, LocalTime time) implements Storable {

    @Override
    public String toCsv() {
        return Csv.join(memberId, date, time);
    }

    public static CheckIn fromCsv(String[] f) {
        return new CheckIn(f[0], LocalDate.parse(f[1]), LocalTime.parse(f[2]));
    }
}
