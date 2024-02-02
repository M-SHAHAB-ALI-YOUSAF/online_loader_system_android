package com.shahab12344.loader_system;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Driver_Homepage_Fragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private ToggleButton toggleButtonStatus;
    private LinearLayout linearLayoutOffline;
    private SessionManager sessionManager;
    String email;

    DrawerLayout drawerLayout;
    ProgressDialog progressDialog;
    NavigationView navigationView;
    ImageView hamburgerIcon;


    public Driver_Homepage_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver__homepage_, container, false);

        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getActivity());


//        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigation);
//        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.bottom_home:
//                    return true;
//                case R.id.bottom_rating:
//                    Fragment rating = new Driver_Rating_Fragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.driver_fragment, rating)
//                            .addToBackStack(null).commit();
//                    return true;
//
//                case R.id.bottom_history:
//                    sessionManager.logoutUser();
//                    // Navigate back to the login or splash screen
//                    Intent intent = new Intent(requireActivity(), Login_Registration.class); // Replace with your login activity
//                    startActivity(intent);
////                    Fragment history = new Driver_History_Fragment();
////                    getFragmentManager().beginTransaction()
////                            .replace(R.id.driver_fragment, history)
////                            .addToBackStack(null).commit();
//                    return true;
//
//                case R.id.bottom_profile:
//                    Fragment profile = new driver_vehicle_informationFragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.driver_fragment, profile)
//                            .addToBackStack(null).commit();
//                    return true;
//
//            }
//            return false;
//        });

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
                    UpdateStatusToActive("True");
                    Toast.makeText(getContext(), "ONLINE", Toast.LENGTH_SHORT).show();
                    linearLayoutOffline.setVisibility(View.GONE);

                } else {
                    UpdateStatusToActive("False");
                    linearLayoutOffline.setVisibility(View.VISIBLE);
                }
            }
        });


        //+++++++++++++++++++++++++++++side driver+++++++++++++++++++++++++++++++++++++++++++++++++
        drawerLayout = view.findViewById(R.id.drawerLayout);

        hamburgerIcon = view.findViewById(R.id.hamburger_icon);


        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView = view.findViewById(R.id.nav_view);
        //adding textview header
        View headerView = navigationView.getHeaderView(0);

        // Find the TextView in the header layout by its ID
        TextView headerTextView = headerView.findViewById(R.id.User_name_in_header);
        ImageView profile = headerView.findViewById(R.id.userprofile);
        String profileImageUri = sessionManager.getProfileImageUri();
        if (profileImageUri != null) {
            // Load the profile image using Glide and transform it into a circle
            Glide.with(this)
                    .load("http://10.0.2.2/Cargo_Go/v1/" + profileImageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile);
        }

        // Retrieve the user's first name from SessionManager
        String firstName = sessionManager.getFirstName();

        // Set the first name in the TextView
        headerTextView.setText(firstName);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        return view;
    }

    private void UpdateStatusToActive(String Status) {
            String  url = "http://10.0.2.2/Cargo_Go/v1/updateActiveStatusOfDriver.php";
            // Create a HashMap to hold the updated user data
            Map<String, String> params = new HashMap<>();
            params.put("Driver_Phone_No", sessionManager.getPhoneNumber());
            params.put("is_Active", Status);

            // Send a POST request to your PHP server for updating the user profile
            RequestQueue queue = Volley.newRequestQueue(requireContext());
        //        String url = "http://10.0.2.2/Cargo_Go/v1/updateUser.php"; // Replace with your server URL
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean error = jsonResponse.getBoolean("error");
                                String message = jsonResponse.getString("message");

                                if (!error) {
                                    if(Status == "True"){
                                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            queue.add(stringRequest);
        }



        //+++++++++++++++++++++++side driver+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            switch (item.getItemId()) {
                // Handle other navigation items

                case R.id.Profile:
                    Fragment fragment = new Driver_profile_Fragment();
                    getFragmentManager().beginTransaction().replace(R.id.driver_fragment, fragment).commit();
                    break;

                case R.id.FAQ:
                    Fragment fragment2 = new FaqFragment();
                    getFragmentManager().beginTransaction().replace(R.id.driver_fragment, fragment2).commit();
                    break;

                case R.id.phoneChange:
                    Fragment changephone = new Chnage_Phone_No();
                    getFragmentManager().beginTransaction().replace(R.id.driver_fragment, changephone).commit();
                    break;

                case R.id.logout:
                    sessionManager.logoutUser();
                    // Navigate back to the login or splash screen
                    Intent intent = new Intent(requireActivity(), Login_Registration.class); // Replace with your login activity
                    startActivity(intent);
                    break;
                case R.id.History:
//                Fragment review = new review_and_rating();
//                getFragmentManager().beginTransaction().replace(R.id.bookingfragment, review).commit();
                    break;

                case R.id.delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Remove Account");
                    builder.setMessage("Are you sure you want to remove your account?");


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            removeAccount();

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(getActivity(), "Account NOT removed!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        }

    //+++++++++++++++++++++++++++++++++++++++remove account+++++++++++++++++++++++++++++++++++++++++++++++++
    private void removeAccount() {
        progressDialog.setMessage("Removing User Account...");
        progressDialog.show();

        // Create a StringRequest with POST method
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_DELETE_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            // Parse the JSON response
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("message").matches("true")) {

                                startActivity(new Intent(getActivity(), Login_Registration.class));

                                Toast.makeText(getActivity(), "User removed successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Display a message indicating failure
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // Handle error cases
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(sessionManager.getRole()=="Driver"){
                    email = "Driver_Email";
                }
                else{
                    email = "Email";
                }
                params.put(email, sessionManager.getEmail());
                return params;
            }
        };

        // Add the request to the request queue
        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}