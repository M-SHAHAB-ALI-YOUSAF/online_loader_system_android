package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class driver_homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_homepage);

        //---- PreLogin Fragement ---

        Fragment fragment = new Driver_Homepage_Fragment();

        // Replace the fragment in the container
        getSupportFragmentManager().beginTransaction().replace(R.id.driver_fragment, fragment).commit();
    }
}