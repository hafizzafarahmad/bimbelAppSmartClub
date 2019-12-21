package com.princedev.bimbel.Model;

public class Payment {

    private String name;
    private String total;
    private String status;

    public Payment() {
    }

    public Payment(String name, String total, String status) {
        this.name = name;
        this.total = total;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
