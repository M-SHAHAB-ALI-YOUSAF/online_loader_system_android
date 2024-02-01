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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                    Fragment profile = new driver_vehicle_informationFragment();
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
                    UpdateStatusToActive("True");
                    Toast.makeText(getContext(), "ONLINE", Toast.LENGTH_SHORT).show();
                    linearLayoutOffline.setVisibility(View.GONE);

                } else {
                    UpdateStatusToActive("False");
                    linearLayoutOffline.setVisibility(View.VISIBLE);
                }
            }
        });
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
}