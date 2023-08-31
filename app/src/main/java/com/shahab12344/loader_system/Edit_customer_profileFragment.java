package com.shahab12344.loader_system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Edit_customer_profileFragment extends Fragment {

    ImageView back_to_show_profile;
    public Edit_customer_profileFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_customer_profile, container, false);

        //backbutton
        back_to_show_profile = view.findViewById(R.id.back_to_profile);
        back_to_show_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Show_Profile_customerFragment();
                getFragmentManager().beginTransaction().replace(R.id.bookingfragment, fragment).commit();
            }
        });
        return  view;
    }
}