package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

public class Login_Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

   //++++++++++++++++++++++++++++++++++Activity that handle all login methods first it go to login+++++++++++++++++++++++++++++
        Fragment fragment = new Login_customers();
        getSupportFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }




}