package com.shahab12344.loader_system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class Show_Profile_customerFragment extends Fragment {

    Button btn_edit_profile;
    ImageView back_to_dashboard;
    public Show_Profile_customerFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show__profile_customer, container, false);

        btn_edit_profile = view.findViewById(R.id.edit_profile);
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Edit_customer_profileFragment();
                getFragmentManager().beginTransaction().replace(R.id.bookingfragment, fragment).commit();
            }
        });

        back_to_dashboard = view.findViewById(R.id.showprofile_back);
        back_to_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Dashbaord_Fragment();
                getFragmentManager().beginTransaction().replace(R.id.bookingfragment, fragment).commit();
            }
        });


        return view;
    }
}