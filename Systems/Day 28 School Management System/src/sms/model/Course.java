package sms.model;

import java.io.Serializable;

public class Course implements Identifiable, Serializable {
    private static final long serialVersionUID = 1L;

    private final String code;
    private String title;
    private int units;
    private int capacity;
    private String teacherId; // null when no teacher is assigned

    public Course(String code, String title, int units, int capacity) {
        this.code = code;
        this.title = title;
        this.units = units;
        this.capacity = capacity;
    }

    @Override
    public String getId() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public boolean hasTeacher() {
        return teacherId != null;
    }

    @Override
    public String toString() {
        return code + " - " + title;
    }
}
