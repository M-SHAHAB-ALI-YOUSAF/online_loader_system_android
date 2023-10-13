package com.shahab12344.loader_system;

public class DriverRequstModel {
        private int driverPictureResource; // Resource ID for driver picture
        private String driverName;
        private String carModel;
        private String carNumber;
        private String ridePrice;

        public DriverRequstModel(int driverPictureResource, String driverName, String carModel, String carNumber, String ridePrice) {
            this.driverPictureResource = driverPictureResource;
            this.driverName = driverName;
            this.carModel = carModel;
            this.carNumber = carNumber;
            this.ridePrice = ridePrice;
        }

        public int getDriverPictureResource() {
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
