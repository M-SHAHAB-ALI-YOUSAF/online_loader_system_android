package com.shahab12344.loader_system;

public class Booking {
    private String date;
    private String pickUp;
    private String dropOff;
    private String price;
    private String status;

    public Booking(String date, String pickUp, String dropOff, String price, String status) {
        this.date = date;
        this.pickUp = pickUp;
        this.dropOff = dropOff;
        this.price = price;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getPickUp() {
        return pickUp;
    }

    public String getDropOff() {
        return dropOff;
    }

    public String getPrice() {
        return price;
    }
    public String getStatus() {
        return status;
    }
}

