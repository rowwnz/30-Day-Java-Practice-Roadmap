package gym.service;

import gym.exception.NotFoundException;
import gym.model.FitnessClass;
import gym.model.Member;
import gym.storage.GymData;
import gym.util.Fmt;
import gym.util.Validator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

/** Weekly group classes: scheduling (no trainer double-booking) and member bookings. */
public class ClassService {
    public static final LocalTime OPENING = LocalTime.of(5, 0);
    public static final LocalTime CLOSING = LocalTime.of(23, 0);
    public static final int MAX_CAPACITY = 50;
    public static final Comparator<FitnessClass> BY_SCHEDULE =
            Comparator.comparing(FitnessClass::getDay).thenComparing(FitnessClass::getStartTime);

    private final GymData data;
    private final MemberService members;
    private final TrainerService trainers;

    public ClassService(GymData data, MemberService members, TrainerService trainers) {
        this.data = data;
        this.members = members;
        this.trainers = trainers;
    }

    public FitnessClass create(String name, String trainerId, DayOfWeek day, LocalTime start, int minutes, int capacity) {
        Validator.require(!name.isBlank(), "Class name is required.");
        String trainer = trainers.find(trainerId).getId();
        validateSlot(null, trainer, day, start, minutes);
        validateCapacity(capacity, 0);
        FitnessClass fitnessClass = new FitnessClass(data.nextId("CLS", 3), name.trim(), trainer, day, start, minutes, capacity);
        data.getClasses().put(fitnessClass.getId(), fitnessClass);
        return fitnessClass;
    }

    public void reschedule(String classId, String trainerId, DayOfWeek day, LocalTime start, int minutes) {
        FitnessClass fitnessClass = find(classId);
        String trainer = trainers.find(trainerId).getId();
        validateSlot(fitnessClass.getId(), trainer, day, start, minutes);
        fitnessClass.reschedule(trainer, day, start, minutes);
    }

    public void changeCapacity(String classId, int capacity) {
        FitnessClass fitnessClass = find(classId);
        validateCapacity(capacity, fitnessClass.getBookings().size());
        fitnessClass.setCapacity(capacity);
    }

    public FitnessClass delete(String classId) {
        FitnessClass fitnessClass = find(classId);
        data.getClasses().remove(fitnessClass.getId());
        return fitnessClass;
    }

    public void book(String classId, String memberId) {
        FitnessClass target = find(classId);
        Member member = members.find(memberId);
        Validator.require(member.isActive(LocalDate.now()),
                member.getFullName() + " needs an active membership to book classes.");
        Validator.require(!target.getBookings().contains(member.getId()),
                member.getFullName() + " is already booked in " + target.getName() + ".");
        Validator.require(!target.isFull(), target.getName() + " is full (" + target.getCapacity() + " slots).");
        for (FitnessClass other : classesOf(member.getId())) {
            Validator.require(!other.overlaps(target.getDay(), target.getStartTime(), target.getDurationMinutes()),
                    "Schedule conflict: " + member.getFullName() + " is booked in " + other.getName()
                            + " on " + Fmt.day(other.getDay()) + " " + other.getTimeSlot() + ".");
        }
        target.addBooking(member.getId());
    }

    public void cancel(String classId, String memberId) {
        FitnessClass target = find(classId);
        String id = memberId.trim().toUpperCase();
        Validator.require(target.removeBooking(id), id + " is not booked in " + target.getName() + ".");
    }

    public FitnessClass find(String classId) {
        FitnessClass fitnessClass = data.getClasses().get(classId.trim().toUpperCase());
        if (fitnessClass == null) throw new NotFoundException("No class with ID '" + classId + "'.");
        return fitnessClass;
    }

    public List<FitnessClass> schedule() {
        return data.getClasses().values().stream().sorted(BY_SCHEDULE).toList();
    }

    public List<FitnessClass> classesOf(String memberId) {
        return data.getClasses().values().stream()
                .filter(c -> c.getBookings().contains(memberId))
                .sorted(BY_SCHEDULE)
                .toList();
    }

    private void validateSlot(String selfId, String trainerId, DayOfWeek day, LocalTime start, int minutes) {
        Validator.require(minutes >= 15 && minutes <= 180, "Duration must be 15 to 180 minutes.");
        LocalTime end = start.plusMinutes(minutes);
        Validator.require(!start.isBefore(OPENING) && end.isAfter(start) && !end.isAfter(CLOSING),
                "Classes must run within gym hours (" + OPENING + " to " + CLOSING + ").");
        for (FitnessClass other : data.getClasses().values()) {
            if (other.getId().equals(selfId) || !other.getTrainerId().equals(trainerId)) continue;
            Validator.require(!other.overlaps(day, start, minutes), trainers.nameOf(trainerId)
                    + " already teaches " + other.getName() + " on " + Fmt.day(other.getDay()) + " " + other.getTimeSlot() + ".");
        }
    }

    private static void validateCapacity(int capacity, int booked) {
        Validator.require(capacity >= 1 && capacity <= MAX_CAPACITY, "Capacity must be 1 to " + MAX_CAPACITY + ".");
        Validator.require(capacity >= booked, "Capacity cannot be lower than the " + booked + " members already booked.");
    }
}
