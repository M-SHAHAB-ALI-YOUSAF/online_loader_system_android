package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class driver_or_customer extends AppCompatActivity {
    Button driver, passenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_or_customer);

        driver = findViewById(R.id.driver);
        passenger = findViewById(R.id.passenger);

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driver = new Intent(driver_or_customer.this, driver_homepage.class);
                startActivity(driver);
            }
        });


        passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passenger = new Intent(driver_or_customer.this, Login_Registration.class);
                startActivity(passenger);
            }
        });


    }
}