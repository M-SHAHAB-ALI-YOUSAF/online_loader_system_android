package com.shahab12344.loader_system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class choiceFragment extends Fragment {
    LinearLayout passenger, driver;


    public choiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choice, container, false);


        passenger = view.findViewById(R.id.loginascustomer);
        passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Login_customers fragmentB = new Login_customers();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.login_RegFragmentContainer, fragmentB); // R.id.fragment_container is the container view in your layout
                transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
                transaction.commit();

            }
        });


        driver = view.findViewById(R.id.loginasdriver);
        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signup_driverFragment fragmentdriver = new Signup_driverFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.login_RegFragmentContainer, fragmentdriver); // R.id.fragment_container is the container view in your layout
                transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
                transaction.commit();
            }
        });

             return view;
    }
}