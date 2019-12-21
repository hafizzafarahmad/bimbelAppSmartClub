package com.princedev.bimbel.Model;

public class Report {

    private String date;
    private String time;
    private String classroom;
    private String teacher;
    private String note;

    public Report() {
    }

    public Report(String date, String time, String classroom, String teacher, String note) {
        this.date = date;
        this.time = time;
        this.classroom = classroom;
        this.teacher = teacher;
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
