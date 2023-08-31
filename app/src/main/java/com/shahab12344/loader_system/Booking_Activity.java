package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class Booking_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        Fragment fragment = new Show_Profile_customerFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.bookingfragment, fragment).commit();
    }
}