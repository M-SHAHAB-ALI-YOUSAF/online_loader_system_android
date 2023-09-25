package com.shahab12344.loader_system;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ComplaintFragment extends Fragment {

    Button btn_submit_complaint;
    ImageView back_to_home;

    public ComplaintFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_complaint, container, false);

        //button submit
        btn_submit_complaint = view.findViewById(R.id.btn_complaint);
//        Fragment fragment = new PreLoginFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
        btn_submit_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Complaint submitted", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(getActivity(), Booking_Activity.class);
                startActivity(home);


            }
        });

        back_to_home = view.findViewById(R.id.complaintback);
        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getActivity(), Booking_Activity.class);
                startActivity(home);

            }
        });

        return view;
    }
}