package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class complaint extends AppCompatActivity {
    //variables
    Button complaint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        //getting id
        complaint = findViewById(R.id.complaint);

        //button click
        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent faq = new Intent(complaint.this, Faq.class);
                startActivity(faq);
            }
        });
    }
}