package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//delete krna ha////////////////////////////////////////////////////////////////
public class Login_customer extends AppCompatActivity {

    //variables
    Button go_to_signup, go_to_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);


        //getting ids
        go_to_signup = findViewById(R.id.reg_go_btn);
        go_to_otp = findViewById(R.id.get_otp);



        //go to signup button click
        go_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUp_customer = new Intent(Login_customer.this, SignUp_customer.class);
                startActivity(SignUp_customer);
            }
        });

        //get otp button click
        go_to_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OTP_screen = new Intent(Login_customer.this, OTP_screen.class);
                startActivity(OTP_screen);
            }
        });
    }
}