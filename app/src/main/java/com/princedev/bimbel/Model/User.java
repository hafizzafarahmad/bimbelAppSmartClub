package com.princedev.bimbel.Model;

public class User {

    private String ni;
    private String password;
    private String nama;
    private String address;
    private String ttl;
    private String profilePhoto;
    private String phone;
    private String study;
    private String status;
    private String classroom;
    private String parent;
    private String phoneParent;


    public User() {
    }

    public User(String ni, String password, String nama, String address, String ttl, String profilePhoto,
                String phone, String study, String status, String classroom, String parent, String phoneParent) {
        this.ni = ni;
        this.password = password;
        this.nama = nama;
        this.address = address;
        this.ttl = ttl;
        this.profilePhoto = profilePhoto;
        this.phone = phone;
        this.study = study;
        this.status = status;
        this.classroom = classroom;
        this.parent = parent;
        this.phoneParent = phoneParent;
    }

    public String getNi() {
        return ni;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getPhoneParent() {
        return phoneParent;
    }

    public void setPhoneParent(String phoneParent) {
        this.phoneParent = phoneParent;
    }
}
