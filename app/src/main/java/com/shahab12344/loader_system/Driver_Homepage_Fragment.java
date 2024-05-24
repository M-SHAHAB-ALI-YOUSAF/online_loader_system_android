package com.shahab12344.loader_system;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
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
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Driver_Homepage_Fragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private ToggleButton toggleButtonStatus;
    private LinearLayout linearLayoutOffline;
    private SessionManager sessionManager;
    private RecyclerView recyclerView;
    private CustomListAdapter adapter;
    private List<CustomListItem> itemList;
    private Handler handler;
    private final int FETCH_INTERVAL = 15000;
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

        //customize list


        //++++++++++++++++++++++++++++++++++++++Linear layout for offline button++++++++++++++++++++++++++++++++++++++++
        linearLayoutOffline = view.findViewById(R.id.linearLayoutOffline);

        //++++++++++++++++++++++++++++++++++++++Toggle button++++++++++++++++++++++++++++++++++++++++
        toggleButtonStatus = view.findViewById(R.id.toggleButtonStatus);
        linearLayoutOffline.setVisibility(View.VISIBLE);

        // Set an initial status (offline)
        toggleButtonStatus.setChecked(false);

        ////////////////recycler view++++++++++++++++++++++++++++++++++++++
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_custom_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //++++++++++ Set initial visibility of RecyclerView based on toggle button state+++++++++++++++++++++
        recyclerView.setVisibility(toggleButtonStatus.isChecked() ? View.VISIBLE : View.GONE);

        //++++++++++++++++++++++++++++++++++++++++++++++++++++ Set a listener for the toggle button
        toggleButtonStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    UpdateStatusToActive("True");
                    linearLayoutOffline.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    handler = new Handler(Looper.getMainLooper());
                    fetchRequestFromDb();
                } else {
                    UpdateStatusToActive("False");
                    linearLayoutOffline.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
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
        View headerView = navigationView.getHeaderView(0);
        TextView headerTextView = headerView.findViewById(R.id.User_name_in_header);
        ImageView profile = headerView.findViewById(R.id.userprofile);
        String profileImageUri = sessionManager.getProfileImageUri();
        if (profileImageUri != null) {
            Glide.with(this)
                    .load("http://10.0.2.2/Cargo_Go/v1/" + profileImageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile);
        }

        String firstName = sessionManager.getFirstName();
        headerTextView.setText(firstName);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        return view;
    }

    private Runnable fetchRunnable = new Runnable() {
        @Override
        public void run() {
            fetchRequestFromDb();
            handler.postDelayed(this, FETCH_INTERVAL);
        }
    };

    //++++++++++++++++++++++++++++++++++fetch request from db to show driver+++++++++++++++++++++++++++++

    private void fetchRequestFromDb() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_booking_to_driver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            if (response == null || response.isEmpty()) {
                                Toast.makeText(getActivity(), "Empty response received", Toast.LENGTH_SHORT).show();
                                return;
                            }
                              JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                JSONArray bookingAndCustomerArray = jsonObject.getJSONArray("booking_and_customer_details");
                                List<CustomListItem> bookingAndCustomerList = new ArrayList<>();
                                for (int i = 0; i < bookingAndCustomerArray.length(); i++) {
                                    JSONObject detailsObject = bookingAndCustomerArray.getJSONObject(i);
                                    String bookingId = detailsObject.getString("Booking_ID");
                                    String pickup = detailsObject.getString("Pickup_Location");
                                    String dropoff = detailsObject.getString("Dropoff_Location");
                                    String cost = detailsObject.getString("Total_Cost");
                                    String helper = detailsObject.getString("Helpers");
                                    String bookingid = detailsObject.getString("Booking_ID");
                                    String customerId = detailsObject.getString("Customer_ID");
                                    String customerFirstName = detailsObject.getString("First_Name");
                                    String customerLastName = detailsObject.getString("Last_Name");
                                    String customerName = customerFirstName + " " + customerLastName;
                                    String customerNumber = detailsObject.getString("Phone_No");
                                    bookingAndCustomerList.add(new CustomListItem( customerName, pickup, dropoff, cost, helper, bookingid, customerNumber, customerId));
                                }
                                adapter = new CustomListAdapter(getContext(), bookingAndCustomerList, getFragmentManager());
                                recyclerView.setAdapter(adapter);
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Driver_ID", sessionManager.getUserId());
                return params;
            }
        };

        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


    //++++++++++++++++++++++++++++++++++++++++++++update driver status offline to online_________________+++++++++++
    private void UpdateStatusToActive(String Status) {
        String url = "http://10.0.2.2/Cargo_Go/v1/updateActiveStatusOfDriver.php";
        Map<String, String> params = new HashMap<>();
        params.put("Driver_Phone_No", sessionManager.getPhoneNumber());
        params.put("is_Active", Status);

        RequestQueue queue = Volley.newRequestQueue(requireContext());
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
                                if ("True".equals(Status)) {
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Profile:
                Fragment fragment = new Driver_profile_Fragment();
                FragmentTransaction profile = getFragmentManager().beginTransaction();
                profile.replace(R.id.driver_fragment, fragment);
                profile.addToBackStack(null); // This line adds the transaction to the back stack
                profile.commit();
                break;
            case R.id.FAQ:
                Fragment faq = new FaqFragment();
                FragmentTransaction faqtransaction = getFragmentManager().beginTransaction();
                faqtransaction.replace(R.id.driver_fragment, faq);
                faqtransaction.addToBackStack(null); // This line adds the transaction to the back stack
                faqtransaction.commit();
                break;
            case R.id.phoneChange:
                Fragment changephone = new Chnage_Phone_No();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.driver_fragment, changephone);
                transaction.addToBackStack(null); // This line adds the transaction to the back stack
                transaction.commit();
                break;
            case R.id.logout:
                sessionManager.logoutUser();
                Intent intent = new Intent(requireActivity(), Login_Registration.class);
                startActivity(intent);
                break;
            case R.id.History:
                Fragment history = new Driver_History_Fragment();
                FragmentTransaction historytransaction = getFragmentManager().beginTransaction();
                historytransaction.replace(R.id.driver_fragment, history);
                historytransaction.addToBackStack(null); // This line adds the transaction to the back stack
                historytransaction.commit();

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


    //-------------------------------------------------delete account--------------------------------------
    private void removeAccount() {
        progressDialog.setMessage("Removing User Account...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_DELETE_driver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").matches("true")) {
                                startActivity(new Intent(getActivity(), Login_Registration.class));
                                Toast.makeText(getActivity(), "User removed successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String emailKey = "Email";
                String role = sessionManager.getRole();
                if (role != null && role.equals("Driver")) {
                    emailKey = "Driver_Email";
                }
                params.put(emailKey, sessionManager.getEmail());
                return params;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
