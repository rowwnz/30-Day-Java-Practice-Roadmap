public class Course {

    private final String code;
    private final String title;
    private final int units;
    private final Schedule schedule;
    private Instructor instructor;

    public Course(String code, String title, int units, String day, String time, String room) {
        this.code = code;
        this.title = title;
        this.units = units;
        this.schedule = new Schedule(day, time, room);
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getUnits() {
        return units;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void assignInstructor(Instructor instructor) {
        this.instructor = instructor;
        instructor.addUnits(units);
    }

    @Override
    public String toString() {
        String instructorName = (instructor == null) ? "TBA" : instructor.getName();
        return String.format("%s - %s (%d units) | %s | Instructor: %s",
                code, title, units, schedule, instructorName);
    }
}
