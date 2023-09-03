package com.shahab12344.loader_system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Driver_Homepage_Fragment extends Fragment {

    public Driver_Homepage_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver__homepage_, container, false);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    return true;
                case R.id.bottom_rating:
                    Fragment rating = new Driver_Rating_Fragment();
                    getFragmentManager().beginTransaction().replace(R.id.driver_fragment, rating).commit();
                    return true;

                case R.id.bottom_history:
                    Fragment history = new Driver_History_Fragment();
                    getFragmentManager().beginTransaction().replace(R.id.driver_fragment, history).commit();
                    return true;

                case R.id.bottom_profile:
                    Fragment profile = new Driver_profile_Fragment();
                    getFragmentManager().beginTransaction().replace(R.id.driver_fragment, profile).commit();
                    return true;

            }
            return false;
        });
        return view;
    }
}