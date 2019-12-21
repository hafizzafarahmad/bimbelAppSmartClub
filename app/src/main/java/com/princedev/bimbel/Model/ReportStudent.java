package com.princedev.bimbel.Model;

public class ReportStudent {

    private String classroom;
    private int hadir;
    private String ni;

    public ReportStudent() {
    }

    public ReportStudent(String classroom, int hadir, String ni) {
        this.classroom = classroom;
        this.hadir = hadir;
        this.ni = ni;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getHadir() {
        return hadir;
    }

    public void setHadir(int hadir) {
        this.hadir = hadir;
    }

    public String getNi() {
        return ni;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }
}
