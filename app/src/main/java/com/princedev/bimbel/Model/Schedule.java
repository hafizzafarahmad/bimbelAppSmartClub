package com.princedev.bimbel.Model;

public class Schedule {

    private String date;
    private String time;
    private String classroom;
    private String teacher;

    public Schedule() {
    }

    public Schedule(String date, String time, String classroom, String teacher) {
        this.date = date;
        this.time = time;
        this.classroom = classroom;
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
