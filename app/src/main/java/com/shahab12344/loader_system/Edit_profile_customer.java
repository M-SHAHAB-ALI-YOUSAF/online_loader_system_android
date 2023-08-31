package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Edit_profile_customer extends AppCompatActivity {
    ImageView back_to_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_customer);

        //back button press
        back_to_profile = findViewById(R.id.back_to_profile);
        back_to_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_back = new Intent(Edit_profile_customer.this, UserProfile.class);
                startActivity(go_back);
            }
        });
    }
}