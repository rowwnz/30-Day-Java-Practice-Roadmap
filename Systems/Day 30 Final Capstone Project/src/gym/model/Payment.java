package gym.model;

import gym.util.Csv;
import gym.util.Fmt;

import java.time.LocalDate;

/**
 * One membership payment. Immutable: a receipt never changes after it is issued.
 * memberName is a snapshot so revenue history survives if the member is later deleted.
 */
public record Payment(String id, String memberId, String memberName, Plan plan,
                      double basePrice, double discount, double amountDue,
                      PaymentMethod method, double amountTendered,
                      LocalDate date, LocalDate coverageStart, LocalDate coverageEnd,
                      String processedBy) implements Storable {

    public double change() {
        return Fmt.round2(amountTendered - amountDue);
    }

    @Override
    public String toCsv() {
        return Csv.join(id, memberId, memberName, plan, basePrice, discount, amountDue,
                method, amountTendered, date, coverageStart, coverageEnd, processedBy);
    }

    public static Payment fromCsv(String[] f) {
        return new Payment(f[0], f[1], f[2], Plan.valueOf(f[3]),
                Double.parseDouble(f[4]), Double.parseDouble(f[5]), Double.parseDouble(f[6]),
                PaymentMethod.valueOf(f[7]), Double.parseDouble(f[8]),
                LocalDate.parse(f[9]), LocalDate.parse(f[10]), LocalDate.parse(f[11]), f[12]);
    }
}
