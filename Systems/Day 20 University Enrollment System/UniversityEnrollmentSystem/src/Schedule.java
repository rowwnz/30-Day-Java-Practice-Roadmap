public class Schedule {

    private final String day;
    private final String time;
    private final String room;

    public Schedule(String day, String time, String room) {
        this.day = day;
        this.time = time;
        this.room = room;
    }

    @Override
    public String toString() {
        return day + " " + time + " @ " + room;
    }
}
