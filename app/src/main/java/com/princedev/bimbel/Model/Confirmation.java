package com.princedev.bimbel.Model;

public class Confirmation {

    private String ni;
    private String an;
    private String image;
    private String payment;

    public Confirmation() {
    }

    public Confirmation(String ni, String an, String image, String payment) {
        this.ni = ni;
        this.an = an;
        this.image = image;
        this.payment = payment;
    }

    public String getNi() {
        return ni;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
