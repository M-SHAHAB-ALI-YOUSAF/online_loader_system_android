package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class UserProfile extends AppCompatActivity {
    Button edit_profile;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //edit profile funcationality
        edit_profile = findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit_prof = new Intent(UserProfile.this, Edit_profile_customer.class);
                startActivity(edit_prof);
            }
        });


        //back button press
        back = findViewById(R.id.back_press);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_back = new Intent(UserProfile.this, Dashboard.class);
                startActivity(go_back);
            }
        });
    }
}