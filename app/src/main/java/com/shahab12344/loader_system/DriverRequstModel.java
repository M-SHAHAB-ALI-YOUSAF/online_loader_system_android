package com.shahab12344.loader_system;

public class DriverRequstModel {
    private String driverPictureResource; // URL for driver picture
    private String driverName;
    private String carModel;
    private String carNumber;
    private String ridePrice;
    private boolean inWishlist;
    private String driverId;

    public DriverRequstModel(String driverId,String driverPictureResource, String driverName, String carModel, String carNumber, String ridePrice) {
        this.driverPictureResource = driverPictureResource;
        this.driverName = driverName;
        this.carModel = carModel;
        this.carNumber = carNumber;
        this.ridePrice = ridePrice;
        this.driverId = driverId;
        this.inWishlist = false;
    }
    public String getDriverId() {
        return driverId;
    }

    public boolean isInWishlist() {
        return inWishlist;
    }

    public void setInWishlist(boolean inWishlist) {
        this.inWishlist = inWishlist;
    }
    public String getDriverPictureResource() {
        return driverPictureResource;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getRidePrice() {
        return ridePrice;
    }
}
