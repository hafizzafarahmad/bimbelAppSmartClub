package com.princedev.bimbel.Model;

public class Student {

    private String ni;
    private String note;
    private int hadir;
    private String classRoom;

    public Student() {
    }

    public Student(String ni, String note, int hadir, String classRoom) {
        this.ni = ni;
        this.note = note;
        this.hadir = hadir;
        this.classRoom = classRoom;
    }

    public String getNi() {
        return ni;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getHadir() {
        return hadir;
    }

    public void setHadir(int hadir) {
        this.hadir = hadir;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }
}
