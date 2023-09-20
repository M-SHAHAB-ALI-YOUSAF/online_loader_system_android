package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class driver_or_customer extends AppCompatActivity {
    LinearLayout driver, passenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_or_customer);


        driver = findViewById(R.id.loginasdriver);
        passenger = findViewById(R.id.loginascustomer);

        passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent customer = new Intent(getApplicationContext(), logi)
            }
        });




    }
}