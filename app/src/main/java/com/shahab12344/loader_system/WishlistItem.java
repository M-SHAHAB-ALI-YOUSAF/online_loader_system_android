package com.shahab12344.loader_system;

public class WishlistItem {
    private String imageUrl;
    private String driverName;
    private String driverContact;
    private String date;

    public WishlistItem(String imageUrl, String driverName, String driverContact, String date) {
        this.imageUrl = imageUrl;
        this.driverName = driverName;
        this.driverContact = driverContact;
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverContact() {
        return driverContact;
    }

    public String getDate() {
        return date;
    }
}
