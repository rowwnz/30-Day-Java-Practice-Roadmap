package gym.model;

import gym.util.Csv;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** A weekly group class (e.g. every Monday 06:30) with a standing list of booked members. */
public class FitnessClass implements Storable {
    private final String id;
    private String name;
    private String trainerId;
    private DayOfWeek day;
    private LocalTime startTime;
    private int durationMinutes;
    private int capacity;
    private final Set<String> bookings = new LinkedHashSet<>();

    public FitnessClass(String id, String name, String trainerId, DayOfWeek day,
                        LocalTime startTime, int durationMinutes, int capacity) {
        this.id = id;
        this.name = name;
        this.trainerId = trainerId;
        this.day = day;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getTrainerId() { return trainerId; }
    public DayOfWeek getDay() { return day; }
    public LocalTime getStartTime() { return startTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getCapacity() { return capacity; }
    public Set<String> getBookings() { return Collections.unmodifiableSet(bookings); }

    public LocalTime getEndTime() { return startTime.plusMinutes(durationMinutes); }
    public int getAvailableSlots() { return capacity - bookings.size(); }
    public boolean isFull() { return bookings.size() >= capacity; }

    public void setName(String name) { this.name = name; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public void reschedule(String trainerId, DayOfWeek day, LocalTime startTime, int durationMinutes) {
        this.trainerId = trainerId;
        this.day = day;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }

    public boolean addBooking(String memberId) { return bookings.add(memberId); }
    public boolean removeBooking(String memberId) { return bookings.remove(memberId); }

    /** True if this class and the given time slot are on the same day and their times intersect. */
    public boolean overlaps(DayOfWeek otherDay, LocalTime otherStart, int otherMinutes) {
        if (day != otherDay) return false;
        LocalTime otherEnd = otherStart.plusMinutes(otherMinutes);
        return startTime.isBefore(otherEnd) && otherStart.isBefore(getEndTime());
    }

    public String getTimeSlot() {
        return startTime + "-" + getEndTime();
    }

    @Override
    public String toCsv() {
        return Csv.join(id, name, trainerId, day, startTime, durationMinutes, capacity, String.join(";", bookings));
    }

    public static FitnessClass fromCsv(String[] f) {
        FitnessClass c = new FitnessClass(f[0], f[1], f[2], DayOfWeek.valueOf(f[3]),
                LocalTime.parse(f[4]), Integer.parseInt(f[5]), Integer.parseInt(f[6]));
        if (!f[7].isEmpty()) {
            for (String memberId : f[7].split(";")) c.bookings.add(memberId);
        }
        return c;
    }
}
