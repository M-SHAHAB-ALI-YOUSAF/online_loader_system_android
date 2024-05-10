package com.shahab12344.loader_system;

public class CustomListItem {

    private String customerName;
    private String pickupLocation;
    private String dropoffLocation;
    private String bookingCost;
    private String bookingHelpers;
    private String bookingid;

    private String customerPhoneNo;
    private String customerID;

    public CustomListItem(String customerName, String pickupLocation, String dropoffLocation, String bookingCost, String bookingHelpers, String bookingid,String customerPhoneNo, String customerID) {
        this.customerName = customerName;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.bookingCost = bookingCost;
        this.bookingHelpers = bookingHelpers;
        this.bookingid = bookingid;
        this.customerPhoneNo = customerPhoneNo;
        this.customerID = customerID;
    }

    // Add getter methods here
    public String getCustomerName() {
        return customerName;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public String getBookingCost() {
        return bookingCost;
    }

    public String getBookingHelpers() {
        return bookingHelpers;
    }
    public String getBookingid(){
        return bookingid;
    }

    public String getCustomerPhoneNo(){
        return customerPhoneNo;
    }
    public String getCustomerID(){
        return customerID;
    }
}
