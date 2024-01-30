package com.shahab12344.loader_system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;


public class Driver_profile_Fragment extends Fragment {


    Button btn_edit_profile;
    ImageView back_to_dashboard;
    private SessionManager sessionManager;
    TextView name;

    public Driver_profile_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver_profile_, container, false);


        //session
        sessionManager = new SessionManager(getContext());



        //navigation
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    Fragment profile = new Driver_Homepage_Fragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.driver_fragment, profile)
                            .addToBackStack(null).commit();
                    return true;
                case R.id.bottom_rating:
                    Fragment rating = new Driver_Rating_Fragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.driver_fragment, rating)
                            .addToBackStack(null).commit();
                    return true;

                case R.id.bottom_history:
                    Fragment history = new Driver_History_Fragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.driver_fragment, history)
                            .addToBackStack(null).commit();
                     return true;

                case R.id.bottom_profile:

                    return true;

            }
            return false;
        });

        //click events'

        btn_edit_profile = view.findViewById(R.id.driver_edit_profile);
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Edit_customer_profileFragment();
                getFragmentManager().beginTransaction().replace(R.id.driver_fragment, fragment).commit();
            }
        });

        back_to_dashboard = view.findViewById(R.id.drive_profile_back);
        back_to_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Driver_Homepage_Fragment();
                getFragmentManager().beginTransaction().replace(R.id.login_RegFragmentContainer, fragment).commit();
            }
        });


        //setting data from session ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        TextInputLayout firstNameEditText = view.findViewById(R.id.reg_d_name);
        TextInputLayout lastNameTabTextView = view.findViewById(R.id.last_d_name);
        TextInputLayout emailTabTextView = view.findViewById(R.id.reg_d_email);
        TextInputLayout phoneTabTextView = view.findViewById(R.id.reg_d_Phone);

        // Retrieve user data from SessionManager
        String firstName = sessionManager.getFirstName();
        String lastName = sessionManager.getLastName();
        String email = sessionManager.getEmail();
        String phoneNumber = sessionManager.getPhoneNumber();
        String profileImageUri = sessionManager.getProfileImageUri();
        ImageView imageView = view.findViewById(R.id.profile_image);
        if (profileImageUri != null) {
            // Load the profile image using Glide and transform it into a circle
            Glide.with(this)
                    .load("http://10.0.2.2/Cargo_Go/v1/" + profileImageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }


        // Set the retrieved user data to TextViews
        firstNameEditText.getEditText().setText(firstName);
        lastNameTabTextView.getEditText().setText(lastName);
        emailTabTextView.getEditText().setText(email);
        phoneTabTextView.getEditText().setText(phoneNumber);
        name = view.findViewById(R.id.editText1);
        name.setText(firstName + " " + lastName);

        return view;
    }
}