package com.shahab12344.loader_system;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Driver_Homepage_Fragment extends Fragment {
    private ToggleButton toggleButtonStatus;
    private LinearLayout linearLayoutOffline;
    private SessionManager sessionManager;

    public Driver_Homepage_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver__homepage_, container, false);

        sessionManager = new SessionManager(getContext());

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    return true;
                case R.id.bottom_rating:
                    Fragment rating = new Driver_Rating_Fragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.driver_fragment, rating)
                            .addToBackStack(null).commit();
                    return true;

                case R.id.bottom_history:
                    sessionManager.logoutUser();
                    // Navigate back to the login or splash screen
                    Intent intent = new Intent(requireActivity(), Login_Registration.class); // Replace with your login activity
                    startActivity(intent);
//                    Fragment history = new Driver_History_Fragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.driver_fragment, history)
//                            .addToBackStack(null).commit();
                    return true;

                case R.id.bottom_profile:
                    Fragment profile = new Driver_profile_Fragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.driver_fragment, profile)
                            .addToBackStack(null).commit();
                    return true;

            }
            return false;
        });

        //++++++++++++++++++++++++++++++++++++++Linear layout for offline button++++++++++++++++++++++++++++++++++++++++
        linearLayoutOffline = view.findViewById(R.id.linearLayoutOffline);



        //++++++++++++++++++++++++++++++++++++++Toggle button++++++++++++++++++++++++++++++++++++++++
        toggleButtonStatus = view.findViewById(R.id.toggleButtonStatus);
        linearLayoutOffline.setVisibility(View.VISIBLE);

        // Set an initial status (offline)
        toggleButtonStatus.setChecked(false);

        // Set a listener for the toggle button
        toggleButtonStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle status change here
                if (isChecked) {
                    Toast.makeText(getContext(), "ONLINE", Toast.LENGTH_SHORT).show();
                    linearLayoutOffline.setVisibility(View.GONE);

                } else {
                    linearLayoutOffline.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }
}