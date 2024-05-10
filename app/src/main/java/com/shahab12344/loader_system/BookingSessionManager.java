package com.shahab12344.loader_system;

import android.content.Context;
import android.content.SharedPreferences;

public class BookingSessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "BookingSession";
    private static final String KEY_VEHICLE_NAME = "vehicleName";
    private static final String KEY_DRIVER_NAME = "driverName";
    private static final String KEY_RIDE_COST = "rideCost";
    private static final String KEY_IMAGE_URL = "imageURL";
    private static final String KEY_DRIVER_ID = "driverID";
    private static final String KEY_CUSTOMER_ID = "customerID";
    private static final String KEY_BOOKING_STATUS = "bookingStatus";
    private static final String KEY_PICKUP_LOCATION = "pickupLocation";
    private static final String KEY_DROPOFF_LOCATION = "dropoffLocation";
    private static final String KEY_BOOKING_ID = "bookingid";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    // Constructor
    public BookingSessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Setter methods for additional booking details
    public void setVehicleName(String vehicleName) {
        editor.putString(KEY_VEHICLE_NAME, vehicleName).apply();
    }

    public void setDriverName(String driverName) {
        editor.putString(KEY_DRIVER_NAME, driverName).apply();
    }

    public void setRideCost(String rideCost) {
        editor.putString(KEY_RIDE_COST, rideCost).apply();
    }

    public void setImageURL(String imageURL) {
        editor.putString(KEY_IMAGE_URL, imageURL).apply();
    }

    public void setDriverID(String driverID) {
        editor.putString(KEY_DRIVER_ID, driverID).apply();
    }

    public void setCustomerID(String customerID) {
        editor.putString(KEY_CUSTOMER_ID, customerID).apply();
    }

    public void setBookingStatus(String bookingStatus) {
        editor.putString(KEY_BOOKING_STATUS, bookingStatus).apply();
    }

    public void setPickupLocation(String pickupLocation) {
        editor.putString(KEY_PICKUP_LOCATION, pickupLocation).apply();
    }

    public void setDropoffLocation(String dropoffLocation) {
        editor.putString(KEY_DROPOFF_LOCATION, dropoffLocation).apply();
    }
    public void setKeyBookingId(String bookingId){
        editor.putString(KEY_BOOKING_ID, bookingId).apply();
    }

    // Getter methods for booking details
    public String getKeyBookingId() {
        return sharedPreferences.getString(KEY_BOOKING_ID, "");
    }
    public String getVehicleName() {
        return sharedPreferences.getString(KEY_VEHICLE_NAME, "");
    }

    public String getDriverName() {
        return sharedPreferences.getString(KEY_DRIVER_NAME, "");
    }

    public String getRideCost() {
        return sharedPreferences.getString(KEY_RIDE_COST, "");
    }

    public String getImageURL() {
        return sharedPreferences.getString(KEY_IMAGE_URL, "");
    }

    public String getDriverID() {
        return sharedPreferences.getString(KEY_DRIVER_ID, "");
    }

    public String getCustomerID() {
        return sharedPreferences.getString(KEY_CUSTOMER_ID, "");
    }

    public String getBookingStatus() {
        return sharedPreferences.getString(KEY_BOOKING_STATUS, "");
    }

    public String getPickupLocation() {
        return sharedPreferences.getString(KEY_PICKUP_LOCATION, "");
    }

    public String getDropoffLocation() {
        return sharedPreferences.getString(KEY_DROPOFF_LOCATION, "");
    }

    // Method to clear booking details
    public void clearBookingDetails() {
        editor.clear().apply();
    }
}
