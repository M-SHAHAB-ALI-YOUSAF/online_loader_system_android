package com.shahab12344.loader_system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Show_Profile_customerFragment extends Fragment {

    Button btn_edit_profile;
    ImageView back_to_dashboard;
    TextView name;

    private SessionManager sessionManager;
    public Show_Profile_customerFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show__profile_customer, container, false);



        //session
        sessionManager = new SessionManager(getContext());


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


        TextInputLayout firstNameEditText = view.findViewById(R.id.et_first_name_customer);
        TextInputLayout lastNameTabTextView = view.findViewById(R.id.et_last_name_customer);
        TextInputLayout emailTabTextView = view.findViewById(R.id.et_email_customer);
        TextInputLayout phoneTabTextView = view.findViewById(R.id.et_phone_customer);

// Retrieve user data from SessionManager
        String firstName = sessionManager.getFirstName();
        String lastName = sessionManager.getLastName();
        String email = sessionManager.getEmail();
        String phoneNumber = sessionManager.getPhoneNumber();

// Set the retrieved user data to TextViews
        firstNameEditText.getEditText().setText(firstName);
        lastNameTabTextView.getEditText().setText(lastName);
        emailTabTextView.getEditText().setText(email);
        phoneTabTextView.getEditText().setText(phoneNumber);
        name = view.findViewById(R.id.editText1);
        name.setText(firstName + " " + lastName );
        return view;
    }
}